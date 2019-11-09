package com.pchudzik.blog.examples.springtransactions;

import javax.transaction.Transactional;

public interface LombokThrowingExceptionJdkProxy {
    @Transactional
    void sneakyThrows();

    @Transactional(rollbackOn = CustomCheckedException.class)
    void withRollbackFor();

    @Transactional(dontRollbackOn = CustomCheckedException.class)
    void dontRollbackOn();
}
