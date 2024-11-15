package com.pig4cloud.pig.admin.iot.mq;


import com.pig4cloud.pig.common.core.util.R;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * mqtt相关接口
 */
@RestController
@RequestMapping("/mqtt")
@RequiredArgsConstructor
public class IMqttController {

    private final MqttClient myMqttClient;

    @PostMapping("/send")
    public R<Object> send(String topic, String msg) {
        try {
            MqttMessage message = new MqttMessage(msg.getBytes());
            message.setQos(1);
            myMqttClient.publish(topic, message);
            return R.ok();
        } catch (Exception e) {
            return R.failed();
        }
    }

    /**
     * MQ4IoT支持点对点消息，即如果发送方明确知道该消息只需要给特定的一个设备接收，且知道对端的 clientId，则可以直接发送点对点消息。
     * 点对点消息不需要经过订阅关系匹配，可以简化订阅方的逻辑。点对点消息的 topic 格式规范是  {{parentTopic}}/p2p/{{targetClientId}}
     *
     * @param topic
     * @param msg
     * @param clientId
     * @return
     */
    @PostMapping("/sendP2p")
    public R<Object> sendP2p(String topic, String msg, Long clientId) {
        try {
            MqttMessage message = new MqttMessage(msg.getBytes());
            message.setQos(1);
            myMqttClient.publish(topic + "/p2p/" + clientId, message);
            return R.ok();
        } catch (Exception e) {
            return R.failed();
        }
    }
}
