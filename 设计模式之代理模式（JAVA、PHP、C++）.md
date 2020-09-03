# 设计模式之代理模式（JAVA/PHP/C++）

## 概述
代理模式使用代理对象完成用户请求，屏蔽用户对真实对象的访问。从第三方的角度看，似乎真实对象并不存在，因为他只和代理对象通信。
代理模式也叫做委托模式，它是一项基本设计技巧。许多其他的模式，如状态模式、策略模式、访问者模式本质上是在更特殊的场合采用了委托模式。

## 应用
在软件设计中，使用代理模式的意图有很多。比如，因为安全原因需要屏蔽客户端直接访问真实对象，或者在远程调用中需要使用代理类处理（屏蔽）远程方法调用中的技术细节，也可能为了提升系统性能，对真实对象进行封装，从而达到延迟加载的目的。

## 角色
1. Subject：接口或抽象类，声明真实角色实现的业务方法。
2. RealSubject：实现抽象角色，定义目标角色所要实现的业务逻辑。
3. Proxy：实现抽象角色，是真实角色的代理，通过真实角色的业务逻辑方法来实现抽象方法，并可以附加自己的操作（如，日志记录，时延统计）。

## 分类
1. 静态代理：代理类在程序运行前就编译好，因此称为静态代理。代理对象和被代理对象都要实现相同的接口或者继承相同的父类，一旦接口或者父类添加、修改方法，子类都要统一更改，违背开闭原则，因此静态代理具有一定的局限性。
2. 动态代理：利用JDK的API，动态的在内存中构建代理对象。不需要继承父类，可扩展性高。另：由于PHP中有内置的魔术方法，实现起来会略微直观些（无需继承特殊的类）。

## 实现
### 静态代理
1. 不同语言实现比较一致。以Java代码为例：
```java
/**
 * \file WorkerIf.java
 */
public interface WorkerIf {
    public void work();
}

/**
 * \file Employee.java
 */
public class Employee implements WorkerIf {
    @Override
    public void work()
    {
        System.out.println("do work");
    }
}

/**
 * \file WorkerProxy.java
 */
public class WorkerProxy implements WorkerIf {
    private WorkerIf obj;

    public WorkerIf bind(WorkerIf obj)
    {
        this.obj = obj;
        return this;
    }

    @Override
    public void work()
    {
        System.out.println("static proxy");
        this.obj.work();
    }
}

/**
 * \file WorkerProxyTest.java
 */
public class WorkerProxyTest {
    public static void main(String[] argv)
    {
        WorkerIf proxy = new WorkerProxy().bind(new Employee());
        proxy.work();
    }
}
```

### 动态代理
1. Java实现
```java
/**
 * \file WorkerIf.java
 */
public interface WorkerIf {
    public void work();
}

/**
 * \file Employee.java
 */
public class Employee implements WorkerIf {
    @Override
    public void work()
    {
        System.out.println("do work");
    }
}

/**
 * WorkerProxy.java
 */
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;

public class WorkerProxy implements InvocationHandler {
    private WorkerIf obj;

    public WorkerIf bind(WorkerIf obj)
    {
        this.obj = obj;
        return (WorkerIf) Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                obj.getClass().getInterfaces(),
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable
    {
        System.out.println("dynamic proxy");
        return method.invoke(obj, args);
    }
}

/**
 * \file WorkerProxyTest.java
 */
public class WorkerProxyTest {
    public static void main(String[] argv)
    {
        WorkerIf proxy = new WorkerProxy().bind(new Employee());
        proxy.work();
    }
}
```

2. PHP实现
```php
/**
 * \file WorkerIf.php
 */
interface WorkerIf {
    public function work();
}

/**
 * \file Employee.php
 */
include "WorkerIf.php";
class Employee implements WorkerIf {
    public function work()
    {
        printf("do work");
    }
}

/**
 * \file WorkerProxy.php
 */
include "Employee.php";
class WorkerProxy {
    /** @var WorkerIf */
    private $obj;

    public function bind(WorkerIf $obj)
    {
        $this->obj = $obj;
        return $this;
    }

    public function __call(string $name, array $args)
    {
        $ref = new ReflectionClass($this->obj);
        if ($method = $ref->getMethod($name)) {
            if ($method->isPublic() && !$method->isAbstract()) {
              $rt = $method->invokeArgs($obj, $args);
           }
        }
        return isset($rt)? $rt: null;
    }
}

/**
 * \file WorkerProxyTest.php
 */
include "WorkerProxy.php";
/** @var WorkerIf $proxy */
$proxy = (new WorkerProxy())->bind(new Employee());
$proxy->work();
```

3. C++实现
你是不是迫不及待的要看这里的实现了？不好意思，这里不会有实现代码。
这里提供一个思路：每个RPC的IDL编辑器都实现了代理，屏蔽内部实现细节，这也是代理模式的一大用途，它们是怎么实现的了？没错，就是生成代码，实现所有接口，再转发到一个统一的抽象入口方法。注：本质上Java的动态代理也是以生成代码方式实现的。（具体细节请看后续IDL编译器相关文章）。

