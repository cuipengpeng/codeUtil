#ifndef TEST01_H
#define TEST01_H

#include "string"
using namespace std;

class User{
private:
string username;


public:
    User(string name);
// User(char* name);

    string getUsername();

};

#endif