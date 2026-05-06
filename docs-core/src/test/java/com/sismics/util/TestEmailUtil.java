package com.sismics.util;

import org.junit.Assert;
import org.junit.Test;
import java.util.Date;

public class TestEmailUtil {

    @Test
    public void testMailContentAndFileContent() {
        EmailUtil.MailContent mailContent = new EmailUtil.MailContent();
        Date now = new Date();
        mailContent.setSubject("Test Subject").setDate(now);

        Assert.assertEquals("Test Subject", mailContent.getSubject());
        Assert.assertEquals(now, mailContent.getDate());
        Assert.assertNotNull(mailContent.getFileContentList());
        Assert.assertNull(mailContent.getMessage());

        EmailUtil.FileContent fileContent = new EmailUtil.FileContent();
        Assert.assertNull(fileContent.getName());
        Assert.assertNull(fileContent.getFile());
        Assert.assertEquals(0L, fileContent.getSize());
    }
}