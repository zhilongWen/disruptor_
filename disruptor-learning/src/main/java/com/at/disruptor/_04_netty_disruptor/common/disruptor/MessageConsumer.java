package com.at.disruptor._04_netty_disruptor.common.disruptor;

import com.at.disruptor._04_netty_disruptor.common.entity.TranslatorDataWapper;
import com.lmax.disruptor.WorkHandler;

/**
 * @author zero
 * @create 2023-05-12
 */
public abstract class MessageConsumer implements WorkHandler<TranslatorDataWapper> {

    protected String consumerId;


    public MessageConsumer(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }
}
