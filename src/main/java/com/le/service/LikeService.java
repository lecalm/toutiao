package com.le.service;

import com.le.util.JedisAdapter;
import com.le.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;

    public int getLikeStatus(int userId, int entityType, int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityId);
        if(jedisAdapter.sismember(likeKey,String.valueOf(userId))){
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType,entityId);
        return jedisAdapter.sismember(disLikeKey,String.valueOf(userId)) ? -1:0;
    }

    public long like(int userId, int entityType, int entityId){
        //在喜欢的集合里增加
        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityId);
        jedisAdapter.sadd(likeKey,String.valueOf(userId));
        //将同一用户从相同事件的反对里删除
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType,entityId);
        jedisAdapter.srem(disLikeKey,String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }

    public long disLike(int userId, int entityType, int entityId){
        // 在反对集合里增加
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.sadd(disLikeKey, String.valueOf(userId));
        // 从喜欢里删除
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.srem(likeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);

    }

}
