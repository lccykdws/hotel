package com.laizhong.hotel.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.laizhong.hotel.constant.HotelConstant;
import com.laizhong.hotel.dto.Auth;
import com.laizhong.hotel.dto.LoginInfoDTO;
import com.laizhong.hotel.filter.LoginFilter;
import com.laizhong.hotel.mapper.AccountMapper;
import com.laizhong.hotel.mapper.AccountRoleMapper;
import com.laizhong.hotel.model.Account;
import com.laizhong.hotel.model.ResponseVo;
import com.laizhong.hotel.utils.UUIDUtil;

 

@Service
public class AuthService {

    

    @Value("${frame.auth.timeoutMinutes:60}")
    private int loginTimeoutMinutes;

	@Autowired
	private AccountMapper accountMapper = null;
	@Autowired
	private AccountRoleMapper accountRoleMapper = null;

    private Cache<String, Auth> cache;

    @PostConstruct
    public void init() {
        cache = CacheBuilder.newBuilder()
                .initialCapacity(10)
                .concurrencyLevel(10)
                .expireAfterWrite(loginTimeoutMinutes, TimeUnit.MINUTES)
                .build();
    }
    
    public ResponseVo<LoginInfoDTO> login(String accountId,String pwd) {   	 
		if (StringUtils.isBlank(accountId)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_013);
		}
		if (StringUtils.isBlank(pwd)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_014);
		}
		Account info = accountMapper.getAccountByIdAndPwd(accountId, pwd);      
		if(null==info) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_015);
		}	 
		String token = "X-" + UUIDUtil.getUid();
		Auth dto = new Auth(token, accountId, new Date(), System.currentTimeMillis() + loginTimeoutMinutes * 60000);
        cache.put(token, dto);
        
        LoginInfoDTO login = new LoginInfoDTO();
        login.setAccountId(accountId);
        login.setAccountName(info.getAccountName());
        login.setToken(token);
        login.setRoles(accountRoleMapper.getAccountRoles(accountId));
		return ResponseVo.success(login);
    }

    public Auth getAuthFormRequest(HttpServletRequest request) {
    	String token = LoginFilter.getTokenFromRequest(request);
    	return getAuth(token,false);
    }
    /*public ResponseVo<String> validate(String userName, String psw, Date loginDate) {
    	ResponseVo<String> result = umSao.auth(userName, psw, loginDate);;
        if (HotelConstant.SUCCESS_CODE.equals(result.getCode())) {
            String token = result.getData();
            Auth dto = new Auth(token, userName, loginDate, System.currentTimeMillis() + loginTimeoutMinutes * 60000);
            cache.put(token, dto);
        }
        return result;
    }*/
    
    
    

    public Auth getAuth(String token, boolean refresh) {
        if (StringUtils.isNotEmpty(token)) {
            Auth dto = cache.getIfPresent(token);
            if (dto != null) {
                if (refresh) {
                    dto.setExpireAt(System.currentTimeMillis() + loginTimeoutMinutes * 60000);
                    cache.put(token, dto);
                }
                return dto;
            }
        }
        return null;
    }

    public void logout(String token) {
        if (StringUtils.isNotEmpty(token)) {
            cache.invalidate(token);
        }
    }

    
}
