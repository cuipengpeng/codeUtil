#include <iostream>
#include "../include02/test01.h"
#include "../include/human.h"
#include "../include/studentA.h"
#include "../include/studentB.h"


int main(){
    User user01("zhansan");
    User user02("lisi");
    studentA h001 = studentA();
    studentB h002 = studentB();
    human* h01 = &h001;
    human* h02 = &h002;
    
    human* h03 = new  studentA();
    human* h04 = new studentB();


    cout<<"user01:" <<user01.getUsername()<<endl;
    cout<<"user02:" <<user02.getUsername()<<endl;

    cout<<"A: "<<h01->playBall()<<endl;
    cout<<"B: "<<h02->playBall()<<endl;
    cout<<"----------"<<endl;

    cout<<"A: "<<h03->playBall()<<endl;
    cout<<"B: "<<h04->playBall()<<endl;
    delete h03;
    delete h04;

    system ("pause");
} 