package com.hospital.checkup.bean;

import java.util.List;

public class TestModelBean {


    /**
     * modelExample : 躯干
     * modelId : 88115887
     * modelName : 髋关节
     * modelType : 2
     * modelCode : 6
     * children : [{"modelId":609766,"modelName":"屈曲/伸展","modelExample":"https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3389246145,1192777546&fm=26&gp=0.jpg","modelType":2,"children":[{"modelId":-36254476,"modelName":"AROM","modelType":3,"modelExample":"https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3389246145,1192777546&fm=26&gp=0.jpg","children":"ullamco aute irure magna"},{"modelId":9419317,"modelName":"PROM","modelType":3,"modelExample":"https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3389246145,1192777546&fm=26&gp=0.jpg","children":"velit amet"}]},{"modelId":609766,"modelName":"外展/内收","modelExample":"https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3389246145,1192777546&fm=26&gp=0.jpg","modelType":2,"children":[{"modelId":-36254476,"modelName":"AROM","modelType":3,"modelExample":"https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3389246145,1192777546&fm=26&gp=0.jpg","children":"ullamco aute irure magna"},{"modelId":9419317,"modelName":"PROM","modelType":3,"modelExample":"https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3389246145,1192777546&fm=26&gp=0.jpg","children":"velit amet"}]},{"modelId":609766,"modelName":"内旋/外旋","modelExample":"https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3389246145,1192777546&fm=26&gp=0.jpg","modelType":2,"children":[{"modelId":-36254476,"modelName":"AROM","modelType":3,"modelExample":"https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3389246145,1192777546&fm=26&gp=0.jpg","children":"ullamco aute irure magna"},{"modelId":9419317,"modelName":"PROM","modelType":3,"modelExample":"https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3389246145,1192777546&fm=26&gp=0.jpg","children":"velit amet"}]}]
     */

    private String modelExample;
    private int modelId;
    private String modelName;
    private int modelType;
    private String modelCode;
    private List<ChildrenBeanX> children;

    public String getModelExample() {
        return modelExample;
    }

    public void setModelExample(String modelExample) {
        this.modelExample = modelExample;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getModelType() {
        return modelType;
    }

    public void setModelType(int modelType) {
        this.modelType = modelType;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public List<ChildrenBeanX> getChildren() {
        return children;
    }

    public void setChildren(List<ChildrenBeanX> children) {
        this.children = children;
    }

    public static class ChildrenBeanX {
        /**
         * modelId : 609766
         * modelName : 屈曲/伸展
         * modelExample : https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3389246145,1192777546&fm=26&gp=0.jpg
         * modelType : 2
         * children : [{"modelId":-36254476,"modelName":"AROM","modelType":3,"modelExample":"https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3389246145,1192777546&fm=26&gp=0.jpg","children":"ullamco aute irure magna"},{"modelId":9419317,"modelName":"PROM","modelType":3,"modelExample":"https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3389246145,1192777546&fm=26&gp=0.jpg","children":"velit amet"}]
         */

        private int modelId;
        private String modelName;
        private String modelExample;
        private int modelType;
        private List<ChildrenBean> children;

        public int getModelId() {
            return modelId;
        }

        public void setModelId(int modelId) {
            this.modelId = modelId;
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        public String getModelExample() {
            return modelExample;
        }

        public void setModelExample(String modelExample) {
            this.modelExample = modelExample;
        }

        public int getModelType() {
            return modelType;
        }

        public void setModelType(int modelType) {
            this.modelType = modelType;
        }

        public List<ChildrenBean> getChildren() {
            return children;
        }

        public void setChildren(List<ChildrenBean> children) {
            this.children = children;
        }

        public static class ChildrenBean {
            /**
             * modelId : -36254476
             * modelName : AROM
             * modelType : 3
             * modelExample : https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3389246145,1192777546&fm=26&gp=0.jpg
             * children : ullamco aute irure magna
             */

            private int modelId;
            private String modelName;
            private int modelType;
            private String modelExample;
            private String children;

            public int getModelId() {
                return modelId;
            }

            public void setModelId(int modelId) {
                this.modelId = modelId;
            }

            public String getModelName() {
                return modelName;
            }

            public void setModelName(String modelName) {
                this.modelName = modelName;
            }

            public int getModelType() {
                return modelType;
            }

            public void setModelType(int modelType) {
                this.modelType = modelType;
            }

            public String getModelExample() {
                return modelExample;
            }

            public void setModelExample(String modelExample) {
                this.modelExample = modelExample;
            }

            public String getChildren() {
                return children;
            }

            public void setChildren(String children) {
                this.children = children;
            }
        }
    }
}
