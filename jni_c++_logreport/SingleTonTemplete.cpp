// template_singleton.h

#include <assert.h>

template<typename T>
class TSingleton
{
    protected:
    TSingleton() {}
    ~TSingleton(){}

    public:
    static T& GetInstance();
    static void ReleaseInstance();

    private:
    TSingleton(const TSingleton& kObj){}
    TSingleton& operator=(const TSingleton& kObj){}

    private:
    static T *instance_ptr_;
};

template<typename T>
T* TSingleton<T>::instance_ptr_ = nullptr;

template<typename T>
T& TSingleton<T>::GetInstance()
{
    if (nullptr == instance_ptr_){
    instance_ptr_ = new T;
    assert(nullptr != instance_ptr_);
    }
    return *instance_ptr_;
}

template<typename T>
void TSingleton<T>::ReleaseInstance()
{
    if (nullptr != instance_ptr_){
    delete instance_ptr_;
    instance_ptr_ = nullptr;
    }
}



