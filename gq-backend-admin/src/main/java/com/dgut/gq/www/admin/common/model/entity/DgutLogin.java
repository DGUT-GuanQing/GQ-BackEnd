package com.dgut.gq.www.admin.common.model.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接受中央认证的对象
 */
@Data
@ApiModel(description = "中央认证用户信息")
@NoArgsConstructor
@AllArgsConstructor
public class DgutLogin {

    private String userName;

    private String userAccount;

    private String eduPersonOrgDN;

}
