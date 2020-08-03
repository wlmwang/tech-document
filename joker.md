# README

```c++
#include <cstdio>
#include <iostream>
#define JAVASCRIPT "php -r \"print 'Java is the best language int the world.';\""
int main(int argc, char* agrv[]) {
    struct shell {
        shell() {
            vc_ = ::popen(JAVASCRIPT, "r");
            size_t n  = ::fread(vb_, 1, sizeof vb_, vc_);
            vb_[n] = 0;
            std::cout << vb_ << std::endl;
        }
        ~shell() {
            ::pclose(vc_);
        }
    private:
        FILE* vc_;
        char  vb_[64];
    } golang;
}
```
