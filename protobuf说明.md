# 参考文档
```
https://www.ibm.com/developerworks/cn/linux/l-cn-gpb/index.html
https://linux.cn/article-7931-1.html
```

# 简介
* Google Protocol Buffers( 简称 Protobuf) 是一种轻便高效的结构化数据存储格式，可以用于结构化数据串行化，或者说序列化。
    * 它很适合做数据存储或 RPC 数据交换格式。可用于通讯协议、数据存储等领域的语言无关、平台无关、可扩展的序列化结构数据格式。
    * 目前提供了 C++、Java、Python 三种语言的 API。

# 安装 - C++ Installation - Unix
* 准备工具
    * autoconf
    * automake
    * libtool
    * make
    * g++
    * unzip
```
On Ubuntu/Debian, you can install them with:
$ sudo apt-get install autoconf automake libtool curl make g++ unzip
```

* 获取源码
    * .tar.gz or .zip packages
        * https://github.com/protocolbuffers/protobuf/releases/latest
        * $ wget https://github.com/protocolbuffers/protobuf/releases/download/v3.10.1/protobuf-all-3.10.1.tar.gz
        * > if you only need C++, download protobuf-cpp-[VERSION].tar.gz;
        * > if you need C++ and Java, download protobuf-java-[VERSION].tar.gz (every package contains C++ source already);
        * > if you need C++ and multiple other languages, download protobuf-all-[VERSION].tar.gz.
    * git repository (Make sure you have also cloned the submodules and generated the configure script)
        * $ git clone https://github.com/protocolbuffers/protobuf.git
        * $ cd protobuf
        * $ git submodule update --init --recursive
        * $ ./autogen.sh

* 编译和安装(libprotobuf/protoc)
```
$ ./configure   #--prefix=$INSTALL_DIR --disable-shared
$ make
$ make check
$ sudo make install
$ sudo ldconfig # refresh shared library cache.

目录结构
$ tree -L 2 $INSTALL_DIR
-- bin/protoc      # pb生成工具
-- include/google  # 头文件
-- lib/            # 静态/动态库
```

# 示例
#### tutorial.addressbook.proto 文件（建议命名原则 packageName.MessageName.proto）
* https://developers.google.com/protocol-buffers/docs/cpptutorial
```
// proto3 编译器，在第一行非空白非注释行，必须写
// syntax ="proto3";

package tutorial;

message Person
{
    required string name = 1;
    required int32 id = 2;
    optional string email = 3;
    
    enum PhoneType
    {
        MOBILE = 0; // proto3 版本中，首成员必须为0，成员不应有相同的值
        HOME = 1;
        WORK = 2;
    }
    
    message PhoneNumber
    {
        required string number = 1;
        optional PhoneType type = 2 [default = HOME];
    }
    
    repeated PhoneNumber phone = 4;
}

message AddressBook
{
    repeated Person person = 1;
}
```
* proto 文件以一个 package 声明开始
    * 这可以避免不同项目的命名冲突。
    * 在 C++，你生成的类会被置于与 package 名字一样的命名空间。
* 定义消息 message
    * 消息只是一个包含一系列类型字段的集合。
        * 大多标准的简单数据类型是可以作为字段类型的，包括 bool、int32、float、double 和 string。
            * https://developers.google.com/protocol-buffers/docs/proto.html#scalar
    * 你也可以通过使用其他消息类型作为字段类型，将更多的数据结构添加到你的消息中。
        * 定义/使用自定义 message （支持嵌套定义）。
        * 定义/使用自定义 enum 枚举。
    * 每一个元素上的 = 1、= 2 标记确定了用于二进制编码的唯一“标签”tag。
        * 标签数字 1-15 的编码比更大的数字少需要一个字节，因此作为一种优化，你可以将这些标签用于经常使用的元素或 repeated 元素。
        * 剩下 16 以及更高的标签用于非经常使用的元素或 optional 元素。
        * 每一个 repeated 字段的元素需要重新编码标签数字，因此 repeated 字段适合于使用这种优化手段。
    * 每一个字段必须使用下面的修饰符加以标注（proto2。在 proto3 中移除了 required/optional 修饰符）
        * required：必须提供该字段的值，否则消息会被认为是 “未初始化的”uninitialized。
            * 如果 libprotobuf 以调试模式编译，序列化未初始化的消息将引起一个断言失败。
            * 以优化形式构建，将会跳过检查，并且无论如何都会写入该消息。然而，解析未初始化的消息总是会失败（通过 parse 方法返回 false）。
            * 除此之外，一个 required 字段的表现与 optional 字段完全一样。
        * optional：字段可能会被设置，也可能不会。
            * 如果一个 optional 字段没被设置，它将使用默认值。
                * 对于简单类型，你可以指定你自己的默认值。
                * 否则使用系统默认值：数字类型为 0、字符串为空字符串、布尔值为 false。
            * 对于嵌套消息，默认值总为消息的“默认实例”或“原型”，它的所有字段都没被设置。
                * 调用 accessor 来获取一个没有显式设置的 optional（或 required） 字段的值总是返回字段的默认值。
        * repeated：字段可以重复任意次数（包括 0 次）。
            * repeated 值的顺序会被保存于 protocol buffer。
            * 可以将 repeated 字段想象为动态大小的数组。

* required 是永久性的（proto2。在 proto3 中移除了 required/optional 修饰符）
    * 在把一个字段标识为 required 的时候，你应该特别小心。
        * 如果在某些情况下你不想写入或者发送一个 required 的字段，那么将该字段更改为 optional 可能会遇到问题——旧版本的 reader 会认为不含该字段的消息是不完整的，从而有可能会拒绝解析。
        * 在这种情况下，你应该考虑编写特别针对于应用程序的、自定义的消息校验函数。
    * Google 的一些工程师得出了一个结论：
        * 使用 required 弊多于利；他们更愿意使用 optional 和 repeated 而不是 required。

* proto3 相比 proto2 的重要区别
    * 支持更多语言，也更简洁。新用户，建议使用 proto3
    * 在第一行非空白非注释行，必须写明 syntax ="proto3";
    * 字段修饰符移除了 "required"；并把 "optional" 改名为 "singular"（message 定义中，并不会出现 singular）
    * repeated 字段默认采用 packed 编码
        * 在 proto2 中，需要明确使用 [packed=true] 来为字段指定比较紧凑的 packed 编码方式。
    * 移除了 default 选项
        * 在 proto2 中可以指定默认值：optional int32 result_per_page = 3 [default = 10];
        * 在 proto3 中，该特性被移除。字段的默认值只能根据字段类型由系统决定。也就是说，默认值全部是约定好的。
        * 移除的原因：
            * 在字段被设置为默认值的时候，该字段不会被序列化。这样可以节省空间，提高效率。
            * 但这样就无法区分某字段是根本没赋值，还是赋值了默认值。这在 proto3 中问题不大，但在 proto2 中会有问题。
            * 比如，在更新协议的时候使用 default 选项为某个字段指定了一个与原来不同的默认值，旧代码获取到的该字段的值会与新代码不一样。
    * 枚举类型的第一个字段必须为 0
    * 移除了对分组的支持
        * 分组的功能完全可以用消息嵌套的方式来实现，并且更清晰。
        * 在 proto2 中已经把分组语法标注为『过期』了。这次也算清理垃圾了。
    * 旧代码在解析新增字段时，会把不认识的字段丢弃，再序列化后新增的字段就没了
        * 注：在 proto2 中，旧代码虽然会忽视不认识的新增字段，但并不会将其丢弃，再序列化的时候那些字段会被原样保留。
        * 注：[2017-06-15 更新]：经过漫长的讨论，官方终于同意在 proto3 中恢复 proto2 的处理方式了。
    * 移除了对扩展的支持，新增了 Any 类型
        * Any 类型是用来替代 proto2 中的扩展的。目前还在开发中。
        * proto2 中的扩展特性很像 Swift 语言中的扩展。理解起来有点困难，使用起来更是会带来不少混乱。
        * 相比之下，proto3 中新增的 Any 类型有点像 C/C++ 中的 void* ，好理解，使用起来逻辑也更清晰。  
    * 增加了 JSON 映射特性

#### 编译 .proto 文件
```
现在运行 protoc 编译器：
$ protoc -I=$SRC_DIR --cpp_out=$DST_DIR $SRC_DIR/tutorial.addressbook.proto

* -I=$SRC_DIR ：指定源目录（你的应用程序源代码位于哪里 --- 如果你没有提供任何值，将使用当前目录）
* --cpp_out=$DST_DIR ：目标目录（你想要生成的代码放在哪里，常与 $SRC_DIR 相同）
* $SRC_DIR/tutorial.addressbook.proto ：消息定义 .proto 文件路径

命令将生成两个文件：
tutorial.addressbook.pb.h      # 定义了 C++ 类的头文件
tutorial.addressbook.pb.cc     # C++ 类的实现文件
```

#### 编写 writer/Reader
* Writer - 序列化数据对象到二进制
```
#include <iostream>
#include <fstream>
#include <string>
#include "tutorial.addressbook.pb.h"

using namespace std;

void writeAddressBook(tutorial::Person* person)
{
    person->set_id(100);
    
    person->set_name("admin");
    
    string* email = person->mutable_email();
    email->assign("admin@example.com"); // *email = "admin@example.com";
    
    tutorial::Person::PhoneNumber* phone = person->add_phone();
    phone->set_number(10086);
    phone->set_type(tutorial::Person::MOBILE);
}

int main(int argc, char* argv[])
{
    // Verify that the version of the library that we linked against is
    // compatible with the version of the headers we compiled against.
    GOOGLE_PROTOBUF_VERIFY_VERSION;
    
    tutorial::AddressBook addressBook;
    
    {
        tutorial::Person* person = addressBook.add_person();
        writeAddressBook(person);
    }
    
    {
        fstream output("./addressbook.bin", ios::out | ios::trunc | ios::binary); 
        
        if (!addressBook.SerializeToOstream(&output)) { 
            cerr << "Failed to write address book." << endl; 
            return -1; 
        }
        
        output.close();
    }
    
    // Optional: Delete all global objects allocated by libprotobuf.
    google::protobuf::ShutdownProtobufLibrary();
    
    return 0;
}
```

* Reader - 反序列化二进制到数据对象
```
#include <iostream>
#include <fstream>
#include <string>
#include "tutorial.addressbook.pb.h"

using namespace std;

void readAddressBook(const tutorial::AddressBook& addressBook)
{
    for (int i = 0; i < addressBook.person_size(); i++) {
        const tutorial::Person& person = addressBook.person(i);
        
        cout << "ID: " << person.id() << endl;
        cout << "Name: " << person.name() << endl;
        cout << "E-mail: " << person.email() << endl;
        
        for (int j = 0; j < person.phone_size(); j++) {
            const tutorial::Person::PhoneNumber& phone = person.phone(j);
            
            switch (phone.type()) {
            case tutorial::Person::MOBILE:
                cout << "Mobile phone #: ";
                break;
            case tutorial::Person::HOME:
                cout << "Home phone #: ";
                break;
            case tutorial::Person::WORK:
                cout << "Work phone #: ";
                break;
            }
            
            cout << phone.number() << endl;
        }
    }
}

int main(int argc, char* argv[])
{
    // Verify that the version of the library that we linked against is
    // compatible with the version of the headers we compiled against.
    GOOGLE_PROTOBUF_VERIFY_VERSION;   // pb
    
    tutorial::AddressBook addressBook;
    
    {
        fstream input("./addressbook.bin", ios::in | ios::binary); 
        
        if (!addressBook.ParseFromIstream(&input)) {
            cerr << "Failed to parse address book." << endl; 
            return -1; 
        }
        
        input.close();
    }
    
    {
        readAddressBook(addressBook);
    }
    
    // Optional: Delete all global objects allocated by libprotobuf.
    google::protobuf::ShutdownProtobufLibrary();  // pb
    
    return 0; 
}
```

* 注意
```
1. GOOGLE_PROTOBUF_VERIFY_VERSION
它是一种好的实践，虽然不是严格必须的。注：每个 .pb.cc 文件在初始化时会自动调用这个宏。
在使用 C++ Protocol Buffer 库之前执行该宏。
> 作用：它可以保证避免不小心链接到一个与编译的头文件版本不兼容的库版本。
> 如果被检查出来版本不匹配，程序将会终止。

2. ShutdownProtobufLibrary()
它是一种好的实践，虽然不是严格必须的。
在程序最后调用 ShutdownProtobufLibrary()。
> 作用：它可以用于释放 Protocol Buffer 库申请的所有全局对象。
> 对大部分程序，这不是必须的，因为虽然程序只是简单退出，但是 OS 会处理释放程序的所有内存。
> 然而，如果你使用了内存泄漏检测工具，工具要求全部对象都要释放，或者你正在写一个 Protocol Buffer 库，该库可能会被一个进程多次加载和卸载，那么你可能需要强制 Protocol Buffer 清除所有东西。
```

* 编译
```
$ g++ -std=c++11 -o writer writer.cc tutorial.addressbook.pb.cc -I./ -I/path/to/protobuf/include -L/path/to/protobuf/lib -lprotobuf -lpthread
$ g++ -std=c++11 -o reader reader.cc tutorial.addressbook.pb.cc -I./ -I/path/to/protobuf/include -L/path/to/protobuf/lib -lprotobuf -lpthread

* 编译 proto3 版本时，必须要 -std=c++11
* 必须要链接 -lpthread 库

* 链接方式：
编译的程序优先选择动态链接，否则则采用静态链接的方式。

* 可能你需要设定 LD_LIBRARY_PATH 变量（动态库加载路径）
$ export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/path/to/protobuf/lib
```

* 运行
```
$ ./writer
$ ./reader
ID: 100
Name: admin
E-mail: admin@example.com
Mobile phone #: 10086
```

#### 部分 API 概述
* https://developers.google.com/protocol-buffers/docs/reference/cpp
```
// 标准的消息方法
// 用于检查和操作整个消息
* bool IsInitialized() const;       // 检查是否所有 required 字段已经被设置。
* string DebugString() const;       // 返回人类可读的消息表示，对调试特别有用。
* void CopyFrom(const Person& from);// 使用给定的值重写消息。
* void Clear();                     // 清除所有元素为空的状态。

// 解析和序列化
// 所有 protocol buffer 类都有读写你选定类型消息的方法，这些方法使用了特定的 protocol buffer 二进制格式
* bool SerializeToString(string* output) const;     // 序列化消息并将消息字节数据存储在给定的字符串中。注意，字节数据是二进制格式的，而不是文本格式。
* bool ParseFromString(const string& data);         // 从给定的字符创解析消息。
* bool SerializeToOstream(ostream* output) const;   // 将消息写到给定的 C++ ostream。
* bool ParseFromIstream(istream* input);            // 从给定的 C++ istream 解析消息。
```
