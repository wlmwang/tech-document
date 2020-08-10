# my career

```c++
// my career
#include <cstdio>
#define JOKER "php -r \"print 'Java(PHP) is the best language in the world.';\""
int main(int argc, char* agrv[]) {
    struct bash {
    public:
        bash() {
            c_ = ::popen(JOKER, "r");
            n_[::fread(n_, 1, sizeof n_, c_)] = 0;
            printf("%s\n", n_);
        }
        ~bash() {
            ::pclose(c_);
        }
    private:
        FILE* c_;
        char  n_[64];
    } go;
}

```
