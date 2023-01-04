// #include <iostream>
// using namespace std;

// class Singleton
// {
// public:
//     ~Singleton(){
//         std::cout<<"destructor called!"<<std::endl;
//     }
//     Singleton(const Singleton&)=delete;
//     Singleton& operator=(const Singleton&)=delete;
//     static Singleton& get_instance(){
//         static Singleton instance;
//         return instance;

//     }
//     int age=0;
// private:
//     Singleton(){
//         std::cout<<"constructor called!"<<std::endl;
//     }
// };

// int main(int argc, char *argv[])
// {
//     Singleton& instance_1 = Singleton::get_instance();
//     cout<<"121212"<<endl;
//     Singleton& instance_2 = Singleton::get_instance();
//     system("pause");
//     return 0;
// }


#include <iostream>
#include <memory> // shared_ptr
#include <mutex>  // mutex
using namespace std;
 
// version 2:
// with problems below fixed:
// 1. thread is safe now
// 2. memory doesn't leak

class Singleton{
public:
    typedef std::shared_ptr<Singleton> Ptr;
    ~Singleton(){
        std::cout<<"destructor called!"<<std::endl;
    }
    Singleton(Singleton&)=delete;
    Singleton& operator=(const Singleton&)=delete;
    static Ptr get_instance(){

        // "double checked lock"
        if(m_instance_ptr==nullptr){
            std::lock_guard<std::mutex> lk(m_mutex);
            if(m_instance_ptr == nullptr){
              m_instance_ptr = std::shared_ptr<Singleton>(new Singleton);
            }
        }
        return m_instance_ptr;
    }
    int age=0;

private:
    Singleton(){
        std::cout<<"constructor called!"<<std::endl;
    }
    static Ptr m_instance_ptr;
    static std::mutex m_mutex;
};

// initialization static variables out of class
Singleton::Ptr Singleton::m_instance_ptr = nullptr;
std::mutex Singleton::m_mutex;

int main(){
    Singleton::Ptr instance = Singleton::get_instance();
    // instance.get();
    cout<<"22222"<<endl;
    Singleton::Ptr instance2 = Singleton::get_instance();
    system("pause");
    return 0;
}