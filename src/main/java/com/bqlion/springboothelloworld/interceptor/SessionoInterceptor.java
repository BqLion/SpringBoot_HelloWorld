package com.bqlion.springboothelloworld.interceptor;

import com.bqlion.springboothelloworld.mapper.UserMapper;
import com.bqlion.springboothelloworld.model.User;
import com.bqlion.springboothelloworld.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/* *
 * Created by BqLion on 2019/8/6
 */
//现在的类需要复写三个函数，ALT+Insert 新建三个复写
@Service            //如果不把本类注入到spring的bean里，无法引入UserMapper
public class SessionoInterceptor implements HandlerInterceptor {
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length != 0)                                             //加一句判断。昨天正常今天就不能访问主页了，抛nullpointer exception。
            for (Cookie cookie : cookies) {                      //估计是清理cookies后首次访问没有cookies，所以会抛出异常。
                if (cookie.getName().equals("token")) {     //加后解决
                    String token = cookie.getValue();
                    UserExample userExample = new UserExample();
                    userExample.createCriteria().
                                    andTokenEqualTo(token);                     //userExample + createCriteria + andTokenEqual/between 等可从数据库返回一个符合条件的列表
                    List<User> users = userMapper.selectByExample(userExample);

                    if (users.size() != 0) {
                        request.getSession().setAttribute("user", users.get(0));
                    }
                    break;
                }
            }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
