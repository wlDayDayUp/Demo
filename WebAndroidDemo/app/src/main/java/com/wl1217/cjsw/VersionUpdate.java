package com.wl1217.cjsw;

/**
 * @ProjectName: WlHappy
 * @Package: com.wl1217.mybox.bean
 * @ClassName: VersionUpdate
 * @Description: 类作用描述
 * @Author: wlDayDayUp
 * @CreateDate: 2019-09-06 14:29
 * @UpdateUser: 更新者
 * @UpdateDate: 2019-09-06 14:29
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class VersionUpdate {

    private String success;
    private String msg;
    private String code;
    private String type;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
