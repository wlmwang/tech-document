# 时间框架 - time
* 基础组件
	* 接口
		* java.time.temporal.Temporal - java.time.temporal.TemporalAccessor
		* java.time.temporal.TemporalAdjuster
		* java.time.temporal.TemporalField
		* java.time.temporal.TemporalUnit
		* java.time.temporal.TemporalAmount
		* java.time.chrono.ChronoPeriod
		* java.time.chrono.ChronoLocalDateTime
		* java.time.chrono.ChronoZonedDateTime
		* 特性
			* Temporal - 定义了如何读取和操纵以时间戳建模的值，比如 Instant,LocalDate,LocalDateTime,LocalTime,OffsetTime, etc.
			* TemporalAdjuster - 用更精细的方式操纵日期，不再局限于一次改变一个字段
			* TemporalField - 定义 Temporal 时间类的字段的类别，比如 NANO_OF_SECOND, NANO_OF_DAY, MICRO_OF_SECOND, etc.
				* java.time.ChronoField, java.time.temporal.IsoFields 实现了该接口
					* Instant.now().get(ChronoField.NANO_OF_SECOND)  - 获取当前时间的纳秒值（直接获取纳秒字段）
					* Instant.now().get(ChronoField.MICRO_OF_SECOND)  - 获取当前时间的微妙值（用纳秒字段算出微妙）
			* TemporalUnit - 一般用来指定一个时间量的单位，比如 NANOS, MICROS, SECONDS, etc.
				* java.time.ChronoUnit, java.time.temporal.IsoFields.Unit 实现了该接口
					* Instant.now().plus(7, ChronoUnit.DAYS) - 获取当前时间 1 星期后的时间戳实例
					* Instant.now().until(Instant.now().plusMillis(1000), ChronoUnit.SECONDS) - 获取 1 秒后时间戳与当前时间戳差值
			* TemporalAmount - 表示一段时间 “1天/2小时/30分钟” 等，可用于日期和时间的加减
				* java.time.Duration, java.time.Period 实现了该接口
			* ChronoPeriod - ISO-8601 日历系统中的日期时间，例如 “2年3个月4天” 等
				* java.time.Period 实现了该接口
			* ChronoLocalDateTime - 没有时区信息的日期时间
			* ChronoZonedDateTime - 带有时区信息的日期时间
	* 实现
		* Instant
			* 不可变对象，代表时间线上的一个瞬时点（时间戳），该瞬间点精确到纳秒分辨率。底层使用 long 存储秒、int 存储纳秒
				* 典型的以 Unix 纪元年时间（1970-01-01T00:00:00Z）开始所经历的秒数进行建模
			* 公共接口
				* static Instant now()
					* 获取当前时间戳实例；时区为 ZoneOffset.UTC
					* 内部使用 System.currentTimeMillis() 获取当前毫秒时间戳（native 方法请看“工具类”中解析）
				* static Instant ofEpochSecond() / static ofEpochMilli()
					* 根据秒、纳秒、毫秒参数新建时间戳实例；参数中的时间代表的是纪元后所经历的时间
				* static Instant from(TemporalAccessor temporal)
					* 根据 temporal 实例新建一个时间戳实例，比如 Instant,LocalDate,LocalDateTime,LocalTime,Month,ZoneOffset 等等
				* static Instant parse() 
					* 根据 IOS 字符串时间新建时间戳实例，比如 "2021-01-27T16:26:00Z"
				* boolean isSupported(TemporalField field)
					* 检查时间戳是否支持某个时间字段转换。field 可以是 ChronoField 枚举中的值
				* boolean isSupported(TemporalUnit unit)
					* 检查时间戳是否支持某个时间单位转换。field 可以是 ChronoUnit 枚举中的值
				* Instant with(TemporalField field, long newValue)
					* 基于当前时间戳返回调整后的时间戳对象，直接指定秒、纳秒、毫秒值。比如：将获取当前时间 100 秒后的时间戳实例：
						* Instant.now().with(TemporalField.INSTANT_SECONDS, Instant.now().getEpochSecond() + 100);
						* Instant.now().with(TemporalField.INSTANT_SECONDS, Instant.now().getLong(TemporalField.INSTANT_SECONDS) + 100);
				* Instant plus(long amountToAdd, TemporalUnit unit)
					* 基于当前时间戳返回调整后的时间戳对象，增加一个时间增量，单位由 unit 指定。比如：获取当前时间 1 星期后的时间戳实例：
						* Instant.now().plus(7, ChronoUnit.DAYS);
				* Instant Instant.minus(long amountToSubtract, TemporalUnit unit)
					* 基于当前时间戳返回调整后的时间戳对象，减去一个时间增量，单位由 unit 指定。比如：获取当前时间 1 星期前的时间戳实例：
						* Instant.now().minus(7, ChronoUnit.DAYS);
					* 注：底层调用实现会将其转换成 Instant.plus(-amountToSubtract, unit);
				* long until(Temporal endExclusive, TemporalUnit unit)
					* 计算当前时间戳到 endExclusive 时间戳之间的时间差，返回一个整形值，单位由 unit 指定。比如：获取 1 秒后时间戳与当前时间戳差值
						* Instant.now().until(Instant.now().plusMillis(1000), ChronoUnit.SECONDS);
				* ZonedDateTime atZone(ZoneId zone)
					* 获取当前时间戳上附加时区的日期实例。比如：获取将当前时间戳附加上中国时区
						* Instant.now().atZone(ZoneId.of("+08"));	// ZoneId.of("Asia/Shanghai")
						* Instant.now().atZone(TimeZone.getTimeZone("GMT+08:00").toZoneId());	// TimeZone.getTimeZone("Asia/Shanghai")
						* Instant.now().atZone(ZoneOffset.of("+08"));	// ZoneOffset.ofHours(8)
		* LocalDate
			* 不可变对象，代表一个日期，它不存储或表示时间或时区。年份范围 [-999,999,999, +999,999,999]，月份范围 [1,12]，日子范围 [1,31]
			* 它是对日期的描述，可用于生日；默认使用本地 TZ 时区信息，但它不能代表时间线上的即时信息，因为它不存储时区
		* LocalTime
			* 不可变对象，代表一个时间，纳秒精度；它不存储或表示日期或时区
			* 它是对挂钟上的时间的描述；默认使用本地 TZ 时区信息，但它不能代表时间线上的即时信息，因为它不存储时区
		* LocalDateTime
			* 不可变对象，代表日期时间，时间表示为纳秒精度，通常被视为年月日时分秒；它不存储或表示时区
			* 它是对日子的描述，可用于生日，结合当地时间在挂钟上看到的；默认使用本地 TZ 时区信息，但它不能代表时间线上的即时信息，因为它不存储时区
		* ZonedDateTime
			* 不可变对象，代表日期时间，时间表示为纳秒精度，通常被视为年月日时分秒；内部使用 LocalDateTime 与 ZoneOffset/ZoneId 字段描述
		* Duration
			* 不可变对象，用于以秒和纳秒为单位测量时间量。一般用来表示两个时间 Temporal 之间的一段时间。比如：获取 1 天时间量的毫秒值
				* Duration.ofDays(1).toMillis()
				* Duration.of(1, ChronoUnit.DAYS).toMillis()
				* Duration.between(Instant.now(), Instant.now().plusMillis(1, ChronoUnit.DAYS)).toMillis()
				* Duration.between(LocalTime.parse("17:22"), LocalTime.parse("17:23")).toMillis()
				* Duration.between(LocalDateTime.parse("2021-01-27T00:00:00"), LocalDateTime.parse("2021-01-28T00:00:00")).toMillis()
		* Period
			* 不可变对象，用于测量以年、月和日为单位的时间。一般用来表示两个日期 LocalDate 之间的一段时间。比如：获取 1 天时间量的天数
				* Period.ofDays(1).getDays()
				* Period.parse("P2021Y01M01D").getDays()
				* Period.between(LocalDate.parse("2021-01-27"), LocalDate.parse("2021-01-28")).getDays()
			* 与 Duration 相比，Period 包含年数，月数，天数，而 Duration 只包含秒，纳秒
			* 注：Period.getDays() 只会获取 days 属性值，而不会将年月部分都计算成天数。想要获取两个时间的天数请使用 Duration.toDays()
* 工具类
	* Year
	* Month
	* DayOfWeek
	* YearMonth
	* MonthDay
	* Clock.SystemClock
	* Clock.FixedClock
	* Clock.OffsetClock
	* Clock.TickClock
	* ZoneId
		* 时区 ID
		* ZoneId.getAvailableZoneIds() / ZoneId.of("+08:00")
	* ZoneOffset
		* 一个以 Greenwich/UTC 偏移建模的时区对象，它继承了 ZoneId 类
		* ZoneOffset.ofHours(8), ZoneOffset.of("+08:00") 效果等同于 ZoneId.of("+08:00") 或 TimeZone.getTimeZone("GMT+08:00").toZoneId()
	* java.util.TimeZone
		* 时区设置
			* 1. 代码指定
				* TimeZone.setDefault(TimeZone.getTimeZone("GMT+08"))
			* 2. JVM 参数指定
				* -Duser.timezone=Asia/Shanghai
			* 3. 环境变量指定
				* export TZ=Asia/Shanghai
			* 4. 修改系统时区指定
				* mv /etc/localtime /etc/localtime.backup
				* ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
	* java.time.zone.ZoneRules
		* 定义区域偏移量在单个时区中如何变化的规则，该规则模拟了时区的所有历史和未来过渡
	* java.time.format.DateTimeFormatter
		* DateTimeFormatter.ISO_DATE
		* DateTimeFormatter.ofPattern("yyyy/MM/dd")
		* 用来指定格式化时间，比如：
			* LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"))
			* LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
			* LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS"));
	* java.time.temporal.ValueRange
		* 日期时间字段的有效值范围。所有 TemporalField 实例都具有有效的值范围。例如，ISO dayOfMonth 从 1-28或者31 之间的某个数字
	* java.lang.System
		* Java 有两个时间测量基础调用 System.currentTimeMillis() / System.nanoTime()
			* System.currentTimeMillis() 返回自 Unix 纪元年时间（1970-01-01T00:00:00Z）开始经过的毫秒数
				* 如果计算机没有进行时间同步，那么两个 currentTimeMillis() 比较的结果没有意义；另外，服务集群中的时钟通常无法做到完全同步
				* 还有，currentTimeMillis() 不能保证返回值是单调递增的。因为，时钟会被人为手动回调，也有可能计算机自动同步让时钟出现了倒退
			* System.nanoTime() 返回自某一固定但任意的时间点开始经过的纳秒数。典型的为 JVM 启动时间为起点，该时间点在一次 JVM 运行期间保持不变
				* 因此，即使比较同一台计算机上两个不同 JVM 的 nanoTime() 返回值也毫无意义，更不用说比较不同计算机上的调用结果
				* 使用场景：可以测量单个 JVM 中两个事件之间经过的时间，但是不能用来比较不同 JVM 中的时间
		* static native long currentTimeMillis()
			* jdk/src/share/native/java/lang/System.c:currentTimeMillis()
			* hotspot/src/share/vm/prims/jvm.cpp:JVM_CurrentTimeMillis
			* hotspot/src/os/linux/vm/os_linux.cpp:os::javaTimeMillis()
				* javaTimeMillis() 的核心就是使用 gettimeofday(&time, NULL) 获取 timeval time，返回毫秒值
					* 注：gettimeofday() 没有使用时区参数
		* static native long nanoTime()
			* jdk/src/share/native/java/lang/System.c:nanoTime()
			* hotspot/src/share/vm/prims/jvm.cpp:JVM_NanoTime
			* hotspot/src/os/linux/vm/os_linux.cpp:os::javaTimeNanos()
				* javaTimeNanos() 的核心就是使用 clock_gettime(CLOCK_MONOTONIC, &tp) 获取 timespec tp，返回纳秒值
					* 不支持 CLOCK_MONOTONIC 单调时钟的系统上，使用 gettimeofday(&time, NULL) 获取 timeval time，返回纳秒值
					* 通常情况下，现代 Linux 发行版都支持了单调时钟
		* 注：clock_gettime()/gettimeofday() 在 Linux 中都提供了快捷调用，它们会尽量避免实际的系统调用（无需用户态到内核态的切换），仅仅是内存地址跳转
			* 这种快捷方式，称为 vDSO 虚拟动态共享对象。本质是导出一些函数，把它们映射到进程的地址空间中。用户进程可以像普通共享库中的普通函数一样调用

