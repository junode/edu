package com.junode.edu.user.api;

import com.junode.edu.common.result.ResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${remote.feign.edu-user-boot.name:edu-user-boot}", path = "/user/vfcode")
public interface VerificationCodeRemoteService {


    /**
     *
     * @param telephone
     * @return
     */
    @RequestMapping("sendCodeRemote")
    public ResponseDTO sendCode(@RequestParam("telephone") String telephone);

    /**
     * 判断验证码是否正确
     * @return
     */
    @RequestMapping("checkCodeRemote")
    public ResponseDTO checkCode(@RequestParam("telephone")String telephone,
                                 @RequestParam("code")String code);

}