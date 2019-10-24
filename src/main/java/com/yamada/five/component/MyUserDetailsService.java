package com.yamada.five.component;

import com.yamada.five.bo.UserInfo;
import com.yamada.five.pojo.User;
import com.yamada.five.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) {
            return null;
        }
        User user = userService.registerAndLogin(username);
        if (user == null) {
            return null;
        }
        return new UserInfo(user.getUserId(), user.getStudentId(), user.getPassword(), true, true, true, true);
    }
}
