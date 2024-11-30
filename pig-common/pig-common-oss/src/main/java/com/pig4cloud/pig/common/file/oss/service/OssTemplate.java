/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng (wangiegie@gmail.com)
 */

package com.pig4cloud.pig.common.file.oss.service;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.pig4cloud.pig.common.file.core.FileProperties;
import com.pig4cloud.pig.common.file.core.FileTemplate;
import com.pig4cloud.pig.common.file.oss.OssProperties;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * aws-s3 通用存储操作 支持所有兼容s3协议的云存储: {阿里云OSS，腾讯云COS，七牛云，京东云，minio 等}
 *
 * @author lengleng
 * @author 858695266
 * @date 2020/5/23 6:36 上午
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
public class OssTemplate implements InitializingBean, FileTemplate {

	private final FileProperties properties;
	private AmazonS3 amazonS3;

	public AssumeRoleResponse getStsToken(FileProperties fileProperties) {
		// STS接入地址，例如sts.cn-hangzhou.aliyuncs.com。
		// 从环境变量中获取步骤1生成的RAM用户的访问密钥（AccessKey ID和AccessKey Secret）。
		// 从环境变量中获取步骤3生成的RAM角色的RamRoleArn。
		// 自定义角色会话名称，用来区分不同的令牌，例如可填写为SessionTest。
		String roleSessionName = String.valueOf(UUID.fastUUID());
		// 以下Policy用于限制仅允许使用临时访问凭证向目标存储空间examplebucket下的src目录上传文件。
		// 临时访问凭证最后获得的权限是步骤4设置的角色权限和该Policy设置权限的交集，即仅允许将文件上传至目标存储空间examplebucket下的src目录。
		// 如果policy为空，则用户将获得该角色下所有权限。
		String policy = fileProperties.getOss().getPolicy();
		// 设置临时访问凭证的有效时间为3600秒。
		Long durationSeconds = 3600L;
		try {
			// regionId表示RAM的地域ID。以华东1（杭州）地域为例，regionID填写为cn-hangzhou。也可以保留默认值，默认值为空字符串（""）。
			String regionId = "";
			// 添加endpoint。适用于Java SDK 3.12.0及以上版本。
			DefaultProfile.addEndpoint(regionId, "Sts", fileProperties.getOss().getStsEndpoint());
			// 添加endpoint。适用于Java SDK 3.12.0以下版本。
			// DefaultProfile.addEndpoint("",regionId, "Sts", endpoint);
			// 构造default profile。
			IClientProfile profile = DefaultProfile.getProfile(regionId, fileProperties.getOss().getAccessKey(), fileProperties.getOss().getSecretKey());
			// 构造client。
			DefaultAcsClient client = new DefaultAcsClient(profile);
			final AssumeRoleRequest request = new AssumeRoleRequest();
			// 适用于Java SDK 3.12.0及以上版本。
			request.setSysMethod(MethodType.POST);
			// 适用于Java SDK 3.12.0以下版本。
			//request.setMethod(MethodType.POST);
			request.setRoleArn(fileProperties.getOss().getRoleArn());
			request.setRoleSessionName(roleSessionName);
			request.setPolicy(policy);
			request.setDurationSeconds(durationSeconds);
			final AssumeRoleResponse response = client.getAcsResponse(request);
			System.out.println("Expiration: " + response.getCredentials().getExpiration());
			System.out.println("Access Key Id: " + response.getCredentials().getAccessKeyId());
			System.out.println("Access Key Secret: " + response.getCredentials().getAccessKeySecret());
			System.out.println("Security Token: " + response.getCredentials().getSecurityToken());
			System.out.println("RequestId: " + response.getRequestId());
			return response;
		} catch (ClientException e) {
			System.out.println("Failed：");
			System.out.println("Error code: " + e.getErrCode());
			System.out.println("Error message: " + e.getErrMsg());
			System.out.println("RequestId: " + e.getRequestId());
		}
		return null;
	}

	public URL uploadEncrypt(OssProperties ossProperties, MultipartFile file) {
		// Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
		String endpoint = ossProperties.getEndpoint();
		// 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
		DefaultCredentialProvider credentialsProvider;
		try {
			credentialsProvider = CredentialsProviderFactory.newDefaultCredentialProvider(ossProperties.getAccessKey(), ossProperties.getSecretKey());
		} catch (Exception e) {
			log.error("EnvironmentVariableCredentialsProvider异常" + e);
			throw new RuntimeException("EnvironmentVariableCredentialsProvider异常", e);
		}
		// 填写Bucket名称，例如examplebucket。
		String bucketName = ossProperties.getBucket();
		// 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
		String objectName = ossProperties.getPath() + "/" + RandomUtil.randomString(128) + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		// 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
		// 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
//        String filePath = "D:\\localpath\\examplefile.txt";

		// 创建OSSClient实例。
		OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);

		try {
			byte[] byteArr = file.getBytes();
			InputStream inputStream = new ByteArrayInputStream(byteArr);
			// 创建PutObjectRequest对象。
			com.aliyun.oss.model.PutObjectRequest putObjectRequest = new com.aliyun.oss.model.PutObjectRequest(bucketName, objectName, inputStream);
			// 创建PutObject请求。
			ossClient.putObject(putObjectRequest);
			String key = objectName;
			Date expiration = new Date(new Date().getTime() + 60 * 1000);
			return ossClient.generatePresignedUrl(ossProperties.getBucket(), key, expiration);
		} catch (OSSException oe) {
			System.out.println("Caught an OSSException, which means your request made it to OSS, "
					+ "but was rejected with an error response for some reason.");
			System.out.println("Error Message:" + oe.getErrorMessage());
			System.out.println("Error Code:" + oe.getErrorCode());
			System.out.println("Request ID:" + oe.getRequestId());
			System.out.println("Host ID:" + oe.getHostId());
			throw new RuntimeException();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (ossClient != null) {
				ossClient.shutdown();
			}
		}
	}

	/**
	 * 文件上传封装
	 *
	 * @param file
	 * @return
	 */
	public String upload(MultipartFile file, FileProperties fileProperties) {
		try {
			String fileName = file.getOriginalFilename();
			String prefix = fileName.substring(fileName.lastIndexOf("."));

			try {
				File tempFile = File.createTempFile(fileName, prefix);
				file.transferTo(tempFile);
				amazonS3.putObject(new PutObjectRequest(fileProperties.getOss().getBucket(), "doctor/" + fileName, tempFile)
						.withCannedAcl(CannedAccessControlList.PublicRead));
				GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(fileProperties.getOss().getBucket(), fileName);
				URL url = amazonS3.generatePresignedUrl(urlRequest);
				return url.toString();
			} catch (Exception e) {
				throw new RuntimeException("", e);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 创建bucket
	 * @param bucketName bucket名称
	 */
	@SneakyThrows
	public void createBucket(String bucketName) {
		if (!amazonS3.doesBucketExistV2(bucketName)) {
			amazonS3.createBucket((bucketName));
		}
	}

	/**
	 * 获取全部bucket
	 * <p>
	 *
	 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/ListBuckets">AWS
	 * API Documentation</a>
	 */
	@SneakyThrows
	public List<Bucket> getAllBuckets() {
		return amazonS3.listBuckets();
	}

	/**
	 * @param bucketName bucket名称
	 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/ListBuckets">AWS
	 * API Documentation</a>
	 */
	@SneakyThrows
	public Optional<Bucket> getBucket(String bucketName) {
		return amazonS3.listBuckets().stream().filter(b -> b.getName().equals(bucketName)).findFirst();
	}

	/**
	 * @param bucketName bucket名称
	 * @see <a href=
	 * "http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/DeleteBucket">AWS API
	 * Documentation</a>
	 */
	@SneakyThrows
	public void removeBucket(String bucketName) {
		amazonS3.deleteBucket(bucketName);
	}

	/**
	 * 根据文件前置查询文件
	 * @param bucketName bucket名称
	 * @param prefix 前缀
	 * @param recursive 是否递归查询
	 * @return S3ObjectSummary 列表
	 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/ListObjects">AWS
	 * API Documentation</a>
	 */
	@SneakyThrows
	public List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive) {
		ObjectListing objectListing = amazonS3.listObjects(bucketName, prefix);
		return new ArrayList<>(objectListing.getObjectSummaries());
	}

	/**
	 * 获取文件外链
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param expires 过期时间 <=7
	 * @return url
	 * @see AmazonS3#generatePresignedUrl(String bucketName, String key, Date expiration)
	 */
	@SneakyThrows
	public String getObjectURL(String bucketName, String objectName, Integer expires) {
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, expires);
		URL url = amazonS3.generatePresignedUrl(bucketName, objectName, calendar.getTime());
		return url.toString();
	}

	/**
	 * 获取文件
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @return 二进制流
	 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/GetObject">AWS
	 * API Documentation</a>
	 */
	@SneakyThrows
	public S3Object getObject(String bucketName, String objectName) {
		return amazonS3.getObject(bucketName, objectName);
	}

	/**
	 * 上传文件
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param stream 文件流
	 * @throws Exception
	 */
	public void putObject(String bucketName, String objectName, InputStream stream) throws Exception {
		putObject(bucketName, objectName, stream, stream.available(), "application/octet-stream");
	}

	/**
	 * 上传文件
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param stream 文件流
	 * @param contextType 文件类型
	 * @throws Exception
	 */
	public void putObject(String bucketName, String objectName, InputStream stream, String contextType)
			throws Exception {
		putObject(bucketName, objectName, stream, stream.available(), contextType);
	}

	/**
	 * 上传文件
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param stream 文件流
	 * @param size 大小
	 * @param contextType 类型
	 * @throws Exception
	 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/PutObject">AWS
	 * API Documentation</a>
	 */
	public PutObjectResult putObject(String bucketName, String objectName, InputStream stream, long size,
			String contextType) throws Exception {
		// String fileName = getFileName(objectName);
		byte[] bytes = IOUtils.toByteArray(stream);
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(size);
		objectMetadata.setContentType(contextType);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		// 上传
		return amazonS3.putObject(bucketName, objectName, byteArrayInputStream, objectMetadata);

	}

	/**
	 * 获取文件信息
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/GetObject">AWS
	 * API Documentation</a>
	 */
	public S3Object getObjectInfo(String bucketName, String objectName) throws Exception {
		@Cleanup
		S3Object object = amazonS3.getObject(bucketName, objectName);
		return object;
	}

	/**
	 * 删除文件
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @throws Exception
	 * @see <a href=
	 * "http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/DeleteObject">AWS API
	 * Documentation</a>
	 */
	public void removeObject(String bucketName, String objectName) throws Exception {
		amazonS3.deleteObject(bucketName, objectName);
	}

	@Override
	public void afterPropertiesSet() {
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setMaxConnections(properties.getOss().getMaxConnections());

		AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
				properties.getOss().getEndpoint(), properties.getOss().getRegion());
		AWSCredentials awsCredentials = new BasicAWSCredentials(properties.getOss().getAccessKey(),
				properties.getOss().getSecretKey());
		AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
		this.amazonS3 = AmazonS3Client.builder()
			.withEndpointConfiguration(endpointConfiguration)
			.withClientConfiguration(clientConfiguration)
			.withCredentials(awsCredentialsProvider)
			.disableChunkedEncoding()
			.withPathStyleAccessEnabled(properties.getOss().getPathStyleAccess())
			.build();
	}

}
