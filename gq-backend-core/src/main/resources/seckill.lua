--参数列表
-- 讲座Id
local lectureId = ARGV[1]

-- 用户id
local userId = ARGV[2]

-- 未开始的讲座数量 和用户key
local stockKey = 'gq:lecture:ticket_number:'

local userKey = 'gq:user:is_grab_tickets:'


--业务
--判断库存是否充足
if(tonumber(redis.call('get',stockKey)) <= 0)then
    -- 库存不足
    return 1
end
-- 判断用户是否以及抢过票
if(redis.call('sismember',userKey,userId) == 1)then
    return 2
end

--
-- 保存用户在redis 减少票的数量
redis.call('incrby',stockKey,-1)
redis.call('sadd',userKey,userId)
return 0