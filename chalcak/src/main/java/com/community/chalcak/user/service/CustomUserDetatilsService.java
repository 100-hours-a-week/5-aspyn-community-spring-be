package com.community.chalcak.user.service;

import com.community.chalcak.user.dao.UserDao;
import com.community.chalcak.user.dto.CustomUserDetails;
import com.community.chalcak.user.entity.User;
import com.community.chalcak.user.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetatilsService implements UserDetailsService {

    private final UserDao userDao;

    public CustomUserDetatilsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("username 값 확인 : " + username);

        // DB에서 유저 정보 조회
        User userData = userDao.findByUsername(username);

        System.out.println("검색된 유저: " + userData);

        if (userData != null) {

            UserEntity userEntity = new UserEntity();
            userEntity.setId(userData.getId());
            userEntity.setEmail(userData.getEmail());
            userEntity.setPassword(userData.getPassword());

            //UserDetails에 담아서 return하면 AutneticationManager가 검증 함
            return new CustomUserDetails(userEntity);

        } else {

        System.out.println("유저정보가 null");
        }

        return null;
    }
}
