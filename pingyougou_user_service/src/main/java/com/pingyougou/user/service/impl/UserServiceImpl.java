package com.pingyougou.user.service.impl;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.pingyougou.user.service.UserService;
import com.pingyougou.utils.PageResult;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pingyougou.mapper.TbUserMapper;
import com.pingyougou.pojo.TbUser;
import com.pingyougou.pojo.TbUserExample;
import com.pingyougou.pojo.TbUserExample.Criteria;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import util.HttpClient;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbUser> findAll() {
		return userMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbUser> page=   (Page<TbUser>) userMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbUser user) {
		 /*`password` varchar(32) NOT NULL COMMENT '密码，加密存储',
		 `created` datetime NOT NULL COMMENT '创建时间',
		 `updated` datetime NOT NULL,
		 `source_type` varchar(1) DEFAULT NULL COMMENT '会员来源：1:PC
		 `status` varchar(1) DEFAULT NULL COMMENT '使用状态（Y正常 N非正常）',  //正常状态
		 `is_mobile_check` varchar(1) DEFAULT '0' COMMENT '手机是否验证 （0否  1是）',   //1是*/
		String password = DigestUtils.md5Hex(user.getPassword());
		user.setPassword(password);
		user.setCreated(new Date());
		user.setUpdated(new Date());
		user.setSourceType("1");
		user.setStatus("Y");
		user.setIsMobileCheck("1");
		userMapper.insert(user);
		//用户注册成功后，清除redis缓存中的验证码
        redisTemplate.delete(user.getPhone());


	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbUser user){
		userMapper.updateByPrimaryKey(user);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbUser findOne(Long id){
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			userMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbUser user, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbUserExample example=new TbUserExample();
		Criteria criteria = example.createCriteria();
		
		if(user!=null){			
						if(user.getUsername()!=null && user.getUsername().length()>0){
				criteria.andUsernameLike("%"+user.getUsername()+"%");
			}
			if(user.getPassword()!=null && user.getPassword().length()>0){
				criteria.andPasswordLike("%"+user.getPassword()+"%");
			}
			if(user.getPhone()!=null && user.getPhone().length()>0){
				criteria.andPhoneLike("%"+user.getPhone()+"%");
			}
			if(user.getEmail()!=null && user.getEmail().length()>0){
				criteria.andEmailLike("%"+user.getEmail()+"%");
			}
			if(user.getSourceType()!=null && user.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
			}
			if(user.getNickName()!=null && user.getNickName().length()>0){
				criteria.andNickNameLike("%"+user.getNickName()+"%");
			}
			if(user.getName()!=null && user.getName().length()>0){
				criteria.andNameLike("%"+user.getName()+"%");
			}
			if(user.getStatus()!=null && user.getStatus().length()>0){
				criteria.andStatusLike("%"+user.getStatus()+"%");
			}
			if(user.getHeadPic()!=null && user.getHeadPic().length()>0){
				criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
			}
			if(user.getQq()!=null && user.getQq().length()>0){
				criteria.andQqLike("%"+user.getQq()+"%");
			}
			if(user.getIsMobileCheck()!=null && user.getIsMobileCheck().length()>0){
				criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
			}
			if(user.getIsEmailCheck()!=null && user.getIsEmailCheck().length()>0){
				criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
			}
			if(user.getSex()!=null && user.getSex().length()>0){
				criteria.andSexLike("%"+user.getSex()+"%");
			}
	
		}
		
		Page<TbUser> page= (Page<TbUser>)userMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 发送验证码
	 * @param phone
	 */
	@Autowired
    private RedisTemplate redisTemplate;

	@Override
	public void sendSmsCode(String phone) throws Exception {
        //1、随机生成6位数字验证码  随机取1到9之间的数字
        int num = (int)(Math.random() * 9 + 1);
        String smsCode = RandomStringUtils.randomNumeric(5);
        smsCode= num+smsCode;
        //2、将生成的验证码保存到redis中 15分钟
        redisTemplate.boundValueOps(phone).set(smsCode,15L, TimeUnit.MINUTES);
        //3、调用品优购短信平台接口，发送验证码
        HttpClient httpClient = new HttpClient("http://localhost:7788/sms/sendSms.do");
        httpClient.addParameter("phoneNumbers",phone);
        httpClient.addParameter("signName","品优购");
        httpClient.addParameter("templateCode","SMS_123738164");
        httpClient.addParameter("param","{\"code\":"+smsCode+"}");
        httpClient.post();

        //获取响应结果 {}
        String content = httpClient.getContent();
        System.out.println(content);
        if(content==null){
            throw new RuntimeException("调用短信接口失败");
        }

	}

    @Override
    public boolean checkSmsCode(String phone, String smsCode) {

        String sysCode =(String) redisTemplate.boundValueOps(phone).get();
        if (sysCode==null){
            return false;
        }
        if (!sysCode.equals(smsCode)){
            return false;
        }
        return true;
    }

}
