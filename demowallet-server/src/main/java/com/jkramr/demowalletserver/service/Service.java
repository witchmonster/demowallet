package com.jkramr.demowalletserver.service;

import com.google.protobuf.GeneratedMessageV3;

public interface Service<T extends GeneratedMessageV3, V extends GeneratedMessageV3> {
    V processRequest(T request);
}
