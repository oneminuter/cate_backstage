package com.cate.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cate.dao.AddressDao;
import com.cate.dao.BannerDao;
import com.cate.dao.FoodDao;
import com.cate.dao.OrderDao;
import com.cate.entity.Address;
import com.cate.entity.Banner;
import com.cate.entity.Food;
import com.cate.entity.Order;
import com.cate.model.Header;
import com.cate.service.Index;
import com.cate.util.DateUtil;

@Service
@Transactional
public class IndexImpl implements Index {
	@Autowired
	Header header;
	@Autowired
	BannerDao bannerDao;
	@Autowired
	FoodDao foodDao;
	@Autowired
	Order order;
	@Autowired
	OrderDao orderDao;
	@Autowired
	AddressDao addressDao;
	
	Map<String, Object> map = null;

	@Override
	public JSONObject getSlides(HttpServletRequest request) {
		List<Banner> list = bannerDao.query();

		map = new HashMap<String, Object>();
		if( list.size() > 0 ) {
			header.setSuccess(true);
			map.put("header", header);
			map.put("body", list);
		} else {
			header.setErrorInfo("数据库中没有数据");
			header.setSuccess(false);
			map.put("header", header);
		}
		return JSONObject.fromObject(map);
	}

	@Override
	public JSONObject getFoodList(String classify) {
		List<Food> foodList = foodDao.queryByClassify(classify);

		map = new HashMap<String, Object>();
		if(foodList.size() > 0){
			header.setSuccess(true);
		}else{
			header.setErrorInfo("数据库中没有数据");
			header.setSuccess(false);
		}
		map.put("header", header);
		map.put("body", foodList);
		return JSONObject.fromObject(map);
	}

	@Override
	public JSONObject getDetail(int id) {
		Food food = foodDao.queryById(id);

		map = new HashMap<String, Object>();
		if(food == null){
			header.setSuccess(false);
			header.setErrorInfo("查询错误，返回结果为空");
			map.put("header", header);
		}else{
			header.setSuccess(true);
			map.put("header", header);
			map.put("body", food);
		}
		return JSONObject.fromObject(map);
	}

	@Override
	public JSONObject getCheckInfo(int id, int number) {
		Food food = foodDao.queryById(id);
		String uid = UUID.randomUUID().toString();
		float totalCost = 0; //总花费，不算代金券抵扣
		float payment = 0; //待支付
		
		
		order.setCash(2);
		order.setStoreName(food.getStoreName());
		order.setFoodName(food.getName());
		order.setPackFee((float) 1.5);
		order.setFreight(5);
		order.setFavorablePrice(4);
		
		totalCost = food.getPrice() * number + order.getPackFee() + order.getFreight() - order.getFavorablePrice();
		payment = totalCost - order.getCash();
		order.setFoodCost(food.getPrice() * number);
		order.setTotalCost(totalCost);
		order.setPayment(payment);
		order.setOrderId(uid);
		order.setFoodId(id);
		order.setBuyNumber(number);
		order.setPrice(food.getPrice());
		order.setOrderDate(DateUtil.getTimestamp());;
		order.setState(0);
		order.setPeopleNumber(1);
		order.setOther("");
		

		map = new HashMap<String, Object>();
		if( !orderDao.add(order) ){
			header.setSuccess(false);
			header.setErrorInfo("创建订单失败");
			map.put("header", header);
		}else{
			header.setSuccess(true);
			map.put("header", header);
			map.put("body", order);
		}
		return JSONObject.fromObject(map);
	}

	@Override
	public JSONObject addReceiveAddress(Address address) {
		if( !addressDao.add(address) ){
			header.setErrorInfo("添加收货地址失败");
			header.setSuccess(false);
		}else{
			header.setSuccess(true);
		}
		map = new HashMap<String, Object>();
		map.put("header", header);
		return JSONObject.fromObject(map);
	}

	@Override
	public JSONObject getAddressList(int userId) {
		map = new HashMap<String, Object>();
		List<Address> list = addressDao.queryAll(userId);
		if( list == null ){
			header.setSuccess(false);
			header.setErrorInfo("数据为空");
			map.put("header", header);
		}else{
			header.setSuccess(true);
			map.put("header", header);
			map.put("body", list);
		}
		return JSONObject.fromObject(map);
	}
	
}
