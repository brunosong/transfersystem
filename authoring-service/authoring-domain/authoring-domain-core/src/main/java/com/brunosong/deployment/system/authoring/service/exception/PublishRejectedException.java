package com.brunosong.deployment.system.authoring.service.exception;

/** 공개할 수 없다(바뀐 것이 없거나, 초안이 어긋났다). 다시 불러와도 같은 결과라 API 에서는 422 가 된다 */
public class PublishRejectedException extends RuntimeException {

    public PublishRejectedException(String message) {
        super(message);
    }
}
