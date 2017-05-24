package com.cate.service;

import net.sf.json.JSONObject;

public interface UserCenter {
	//获取订单列表
	public JSONObject getOrderList(int userId);
	//注册 
	public JSONObject register(String phone, String password);
	//登录
	public JSONObject login(String phone, String password);
}
