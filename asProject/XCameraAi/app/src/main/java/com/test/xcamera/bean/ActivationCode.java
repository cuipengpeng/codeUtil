package com.test.xcamera.bean;


import com.framwork.base.BaseResponse;

import java.io.Serializable;

/**
 * Author: mz
 * Time:  2019/9/26
 */
public class ActivationCode extends BaseResponse {


    /**
     * data : {"activationCode":"U2FsdGVkX19Z6DPSCA4xwycsjkJbMSrFioDWvOxb+fQ=","activationId":"U2FsdGVkX197ABhViVYF9HZWCZchuFwW8H9veXDnxFk="}
     */

    private ActivationCodeDetail data;

    public ActivationCodeDetail getData() {
        return data;
    }

    public void setData(ActivationCodeDetail data) {
        this.data = data;
    }

    public static class ActivationCodeDetail implements Serializable {
        /**
         * activationCode : U2FsdGVkX19Z6DPSCA4xwycsjkJbMSrFioDWvOxb+fQ=
         * activationId : U2FsdGVkX197ABhViVYF9HZWCZchuFwW8H9veXDnxFk=
         */

        private String activationCode;
        private String activationId;

        public String getActivationCode() {
            return activationCode;
        }

        public void setActivationCode(String activationCode) {
            this.activationCode = activationCode;
        }

        public String getActivationId() {
            return activationId;
        }

        public void setActivationId(String activationId) {
            this.activationId = activationId;
        }
    }
}
