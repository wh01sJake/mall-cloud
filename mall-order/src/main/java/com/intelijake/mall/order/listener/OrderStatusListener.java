package com.intelijake.mall.order.listener;

import com.intelijake.mall.common.constant.MqConstant;
import com.intelijake.mall.common.util.MultiDelayMessage;
import com.intelijake.mall.order.service.ICustomerOrderService;
import com.intelijake.mall.pojo.CustomerOrder;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Order Status Listener
 * Handles delayed order processing and automatic order cancellation
 * 
 * @author Jake
 * @since 2025-07-22
 */
@Component
public class OrderStatusListener {

    @Autowired
    private ICustomerOrderService customerOrderService;

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    /**
     * Listen for delayed order messages
     * Handles multi-stage order timeout processing with automatic cancellation
     * 
     * TEMPORARILY DISABLED - Enable this when RabbitMQ delayed message plugin is installed
     * or implement alternative delay mechanism
     */
    // @RabbitListener(bindings = @QueueBinding(
    //         value = @Queue(value = MqConstant.DELAY_ORDER_QUEUE, durable = "true"),
    //         exchange = @Exchange(name = MqConstant.DELAY_EXCHANGE, type = "topic", durable = "true"),
    //         key = MqConstant.DELAY_ORDER_ROUTING_KEY
    // ))
    public void listenDelayMsg(MultiDelayMessage<Long> message) {
        
        System.out.println("OrderStatusListener.listening - Processing delayed order message");

        // Get order number from message
        Long orderNo = message.getData();
        
        if (orderNo == null) {
            System.err.println("OrderStatusListener: Received null order number");
            return;
        }

        System.out.println("OrderStatusListener: Processing order " + orderNo);

        // Get current order status
        CustomerOrder order = customerOrderService.getById(orderNo);

        // If order doesn't exist or is already paid (status > 1), no action needed
        if (order == null) {
            System.out.println("OrderStatusListener: Order " + orderNo + " not found, skipping");
            return;
        }
        
        if (order.getStatus() > 1) {
            System.out.println("OrderStatusListener: Order " + orderNo + " already paid (status: " + order.getStatus() + "), skipping");
            return;
        }

        // Check if there are more delay stages
        if (message.hasNextDelay()) {
            
            List<Long> delayMillis = message.getDelayMillis();
            System.out.println("OrderStatusListener: Remaining delays for order " + orderNo + ": " + delayMillis);

            Long delay = message.removeNextDelay();
            System.out.println("OrderStatusListener: Scheduling next delay for order " + orderNo + ": " + delay + "ms");

            // Send message for next delay stage (only if RabbitMQ is available)
            if (rabbitTemplate != null) {
                rabbitTemplate.convertAndSend(
                    MqConstant.DELAY_EXCHANGE,
                    MqConstant.DELAY_ORDER_ROUTING_KEY,
                    message,
                    new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            // Use TTL instead of x-delayed-message plugin
                            message.getMessageProperties().setExpiration(String.valueOf(delay));
                            return message;
                        }
                    }
                );
            } else {
                System.out.println("OrderStatusListener: RabbitMQ not available, skipping message send");
            }

        } else {
            // No more delays, cancel the unpaid order
            System.out.println("OrderStatusListener: Final timeout reached for order " + orderNo + ", cancelling order");
            
            try {
                customerOrderService.cancel(orderNo);
                System.out.println("OrderStatusListener: Successfully cancelled order " + orderNo);
            } catch (Exception e) {
                System.err.println("OrderStatusListener: Failed to cancel order " + orderNo + ", error: " + e.getMessage());
            }
        }
    }
}
