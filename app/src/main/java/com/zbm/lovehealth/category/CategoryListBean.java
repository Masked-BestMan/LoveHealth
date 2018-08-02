package com.zbm.lovehealth.category;

import com.zbm.lovehealth.AbstractDataBean;

import java.util.List;

public class CategoryListBean extends AbstractDataBean {


    /**
     * showapi_res_error :
     * showapi_res_id : abd36cfc12034a1c8ad9a14407dd7579
     * showapi_res_code : 0
     * showapi_res_body : {"list":[{"name":"综合资讯","id":"1"},{"name":"疾病资讯","id":"2"},{"name":"食品资讯","id":"3"}],"ret_code":0}
     */

    private String showapi_res_error;
    private int showapi_res_code;
    private ShowapiResBodyBean showapi_res_body;

    public String getShowapi_res_error() {
        return showapi_res_error;
    }

    public void setShowapi_res_error(String showapi_res_error) {
        this.showapi_res_error = showapi_res_error;
    }

    public int getShowapi_res_code() {
        return showapi_res_code;
    }

    public void setShowapi_res_code(int showapi_res_code) {
        this.showapi_res_code = showapi_res_code;
    }

    public ShowapiResBodyBean getShowapi_res_body() {
        return showapi_res_body;
    }

    public void setShowapi_res_body(ShowapiResBodyBean showapi_res_body) {
        this.showapi_res_body = showapi_res_body;
    }

    public static class ShowapiResBodyBean {
        /**
         * list : [{"name":"综合资讯","id":"1"},{"name":"疾病资讯","id":"2"},{"name":"食品资讯","id":"3"}]
         * ret_code : 0
         */

        private int ret_code;
        private List<ListBean> list;

        public int getRet_code() {
            return ret_code;
        }

        public void setRet_code(int ret_code) {
            this.ret_code = ret_code;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * name : 综合资讯
             * id : 1
             */

            private String name;
            private String id;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }

    @Override
    public int getObjectSize() {
        StringBuilder sb=new StringBuilder();
        sb.append(getShowapi_res_error())
                .append(getShowapi_res_code());
        for (ShowapiResBodyBean.ListBean listBean:showapi_res_body.list){
            sb.append(listBean.id).append(listBean.name);
        }
        return sb.toString().getBytes().length;
    }
}
