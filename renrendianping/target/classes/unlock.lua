-- 锁的key
local key = KEYS[1]
-- 传入的当前线程标识
local threadId = ARGV[1]
-- 获取锁中的线程标识
local id = redis.call('get',key)
-- 比较
if(id == threadId) then
    return redis.call('del',key)
end
return 0
