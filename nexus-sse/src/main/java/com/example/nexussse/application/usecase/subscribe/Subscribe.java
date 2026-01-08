package com.example.nexussse.application.usecase.subscribe;

import com.example.nexussse.application.usecase.subscribe.dto.SubscribeReq;
import com.example.nexussse.application.usecase.subscribe.dto.SubscribeRes;

public interface Subscribe {
    
    SubscribeRes subscribe(SubscribeReq req);
}
