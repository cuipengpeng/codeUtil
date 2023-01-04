#include <string>
#include <iostream>
#include <fstream>
#include <vector>
#include<algorithm>

using namespace std;

class CTestPermutation
{
    public:
    CTestPermutation();
    ~CTestPermutation();
    void DoTest();
    void Permutation(vector<int> vecPermutated,vector<int> vecWaitPermuta);
    private:
};

CTestPermutation::CTestPermutation()
{
    cout<<">>>>>>>>>  222  CTestPermutation::CTestPermutation()"<<endl;
}

CTestPermutation::~CTestPermutation()
{
    cout<<">>>>>>>>>>>>>>>>>>>>>>>>CTestPermutation::~CTestPermutation()"<<endl;
}

void CTestPermutation::DoTest()
{
    cout<<"-------将N个数进行排列组合-------"<<endl<<endl;
    vector<int> vecNums,vecPermutated;
    cout<<"示例数据:";
    for(int i = 1; i < 5;i++)
    {
        vecNums.push_back(i);
        cout<<i<<" ";
    }
    cout<<endl;
    cout<<"排列组合结果："<<endl;
    Permutation(vecPermutated,vecNums);

}

/******************************************************
 @ FunctionNmae:            Permutation
 @ Function:                将N个数进行排列组合
 @ vecPermutated:           已经排列好的数列
 @ vecWaitPermuta:          待排列的数
********************************************************/

void CTestPermutation::Permutation(vector<int> vecPermutated,vector<int> vecWaitPermuta)
{
    if(vecWaitPermuta.size() > 0 )
    {
        for(vector<int>::iterator itNum = vecWaitPermuta.begin();itNum != vecWaitPermuta.end();++itNum)
        {
            vector<int> vecPermutatedTmp = vecPermutated;
            vecPermutatedTmp.push_back(*itNum);

            vector<int> vecWaitPermutaTmp = vecWaitPermuta;
            //删除当前已经加入排列完毕的元素
            vector<int>::iterator retFind = find(vecWaitPermutaTmp.begin(),vecWaitPermutaTmp.end(),*itNum);
            if(vecWaitPermutaTmp.end() != retFind)
            {
                vecWaitPermutaTmp.erase(retFind);
            }
            //继续递归调用排列算法
            Permutation(vecPermutatedTmp,vecWaitPermutaTmp);
        }
    }
    else //一组排列完毕
    {
        //打印排列结果
        cout<<"\t";
        for(int i = 0; i < vecPermutated.size();++i)
        {
            cout<<vecPermutated.at(i)<<" ";
        }
        cout<<endl;
    }
}

int main(int argc, char** argv)
{
    CTestPermutation p;
    p.DoTest();
    return 0;
}



