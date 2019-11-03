package com.pchudzik.blog.examples.springtransactions;

import javax.transaction.Transactional;

public interface LombokThrowingException {
    @Transactional
    void sneakyThrows();

    @Transactional(rollbackOn = CustomCheckedException.class)
    void withRollbackFor();
}
