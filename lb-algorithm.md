
## 几种常用负载均衡算法

### 无状态
1. 随机（Random）
```
	random() % count(servers)
	uuid() % count(servers)
```

2. 加权随机（Weight Random）
```
	// [1, 2, 4]
	sum = sumWeight(servers)
	min = minWeight(servers)
	r = random(min, sum)		// 闭区间 random => [min, sum]
	i = curr = 0
	while i < count(servers)
		curr += servers[i]
		if r <= curr
			return i
		i++
```

3. 哈希（Hash）
```
	hashCode(ip) % count(servers)
	hashCode(identify) % count(servers)
```

4. 一致性（Consistent Hash）
```
	map<int32_t, server> servers	// map<hashCode, server>
	src = hashCode(ip)
	for iter : servers
		if src < iter->first
			return iter->second
```

### 有状态（服务端保存分配进度 || 或集群的分配状态 || 或集群负载状态）
5. 轮询（Round Robin）
```
	g_seq = -1;
	servers[++g_seq%n]		// 顺序
```

6. 加权轮询（Weight Round Robin）
```
	a.普通
		map<int32_t, server> servers 	// map<weight, server>
		gcd = gcdWeight(servers)
		max = maxWeight(servers)

		g_index = -1;
		g_curr = max

		procedure:
			g_index++
			for i = g_index; i < count(servers); i++
				if servers[i].first >= g_curr
					break;
			
			if i == count(servers)
				g_curr -= gcd
				g_index = -1

			if g_curr == 0
				g_curr = max

			return i

		-------------------------------------------------------
		----------servers=[1:a, 2:b, 4:c]----------------------
		-------------------------------------------------------
			before				|	after
		 	g_index++ 	g_curr	|	server 	g_index 	g_curr
		------------------------|-------------------------------
		1	0			4		|	c 		-1			3
		2	0			3		|	c 		-1			2
		3	0			2		|	b 		1			2
		4	2			2		|	c 		-1			1
		5	0			1		|	a 		0			1
		6	1			1		|	b 		1			1
		7	2			1		|	c 		-1 			4
		------------------------|-------------------------------
		g_index = -1
		g_curr = max

	b. 平滑加权轮询（Nginx WRR）
		略
```

7. 忙闲（Idle Status）
```
	list<server> idleServers
	list<server> usingServers
```

8. 最少连接（Least Connections）
```
	map<int, list<server>> servers	// map<count:servers>
```

9. 最少负载（Least Load） 
```
	// serversWeight[k] = load_func(delay, status)
	multimap<float, server>		// map<load:server>
```
