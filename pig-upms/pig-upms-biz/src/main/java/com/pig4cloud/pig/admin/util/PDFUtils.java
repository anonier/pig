package com.pig4cloud.pig.admin.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @author yb
 * Date on 2023/9/18  8:49
 */
@Slf4j
public class PDFUtils {

	/**
	 * 根据pdf模板输出流
	 *
	 * @param templateFilePath 模板目录路径
	 * @param templateFileName 模板文件名
	 * @param resultMap        包含文件字段名和值的map
	 * @return 生成的文件字节流
	 */
	public static ByteArrayOutputStream createPdfStream(String templateFilePath, String templateFileName,
														Map<String, String> resultMap) {

		ByteArrayOutputStream ba = new ByteArrayOutputStream();
		PdfStamper stamp = null;
		PdfReader reader = null;
		try {

			log.debug("file:" + templateFilePath + "/" + templateFileName);
			reader = new PdfReader(templateFilePath + "/" + templateFileName);
			stamp = new PdfStamper(reader, ba);

			//使用字体
			BaseFont bf = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			/* 获取模版中的字段 */
			AcroFields form = stamp.getAcroFields();

			//填充表单
			if (resultMap != null) {
				for (Map.Entry<String, String> entry : resultMap.entrySet()) {

					form.setFieldProperty(entry.getKey(), "textfont", bf, null);
					form.setField(entry.getKey(), entry.getValue() != null ? entry.getValue() : "");
				}
			}
			//不能编辑
			stamp.setFormFlattening(true);

		} catch (IOException e) {
			log.error("文档构建I/O异常", e);
		} catch (DocumentException e) {
			log.error("文档构建异常", e);
		} finally {
			if (stamp != null) {
				try {
					stamp.close();
				} catch (DocumentException e) {
					log.error("流关闭错误", e);
				} catch (IOException e) {
					log.error("流关闭错误", e);
				}
			}
			if (reader != null) {
				reader.close();
			}
		}
		return ba;
	}
}
