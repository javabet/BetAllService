package com.wisp.game.bet.world.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class WorldInitConfig implements InitializingBean {

    public  void afterPropertiesSet() throws Exception
    {
        //需要加载的xml数据
        RestTemplate restTemplate = new RestTemplate();


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> map= new LinkedMultiValueMap<>();
        map.add("UserName", "test_client_0004");
        map.add("ApiUrl", "https://api.gametech222.com/");
        map.add("AgentId", 3);
        map.add("ChannelId", "973606_1");
        map.add("SecKey", "OFZKh0Sji7CtYJUx");
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);

        //String responseBody =  restTemplate.postForObject("http://127.0.0.1:3000/Api/Test/Login",request,String.class);

        System.out.printf("go this...");
        //System.out.printf(responseBody);


        //LoginResponse loginResponse =  restTemplate.postForObject("http://127.0.0.1:3000/Api/Test/Login",request,LoginResponse.class);

        //System.out.printf("go this....2");
    }
}
