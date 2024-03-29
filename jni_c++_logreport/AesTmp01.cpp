/******************************************************************************************
AES-192与AES-256加解密方式与AES-128一样，只是AES-128运算10轮，AES-192与AES-256分别运算12轮和14轮。

文中用到的示例：
明文：32 43 F6 A8 88 5A 30 8D 31 31 98 A2 E0 37 07 34
密钥：2B 7E 15 16 28 AE D2 A6 AB F7 15 88 09 CF 4F 3C
密文：39 25 84 1D 02 DC 09 FB DC 11 85 97 19 6A 0B 32

https://blog.csdn.net/kongtaoxing/article/details/127228448
 ******************************************************************************************/

#include<string>
#include<iostream>
#include<fstream>
#include<vector>
using namespace std;

char Sbox[16][16][3] = {
	    /****    0     1     2     3     4     5     6     7     8     9     a     b     c     d     e     f */
		/*0*/{ "63", "7C", "77", "7B", "F2", "6B", "6F", "C5", "30", "01", "67", "2B", "FE", "D7", "AB", "76" },
		/*1*/{ "CA", "82", "C9", "7D", "FA", "59", "47", "F0", "AD", "D4", "A2", "AF", "9C", "A4", "72", "C0" },
		/*2*/{ "B7", "FD", "93", "26", "36", "3F", "F7", "CC", "34", "A5", "E5", "F1", "71", "D8", "31", "15" },
		/*3*/{ "04", "C7", "23", "C3", "18", "96", "05", "9A", "07", "12", "80", "E2", "EB", "27", "B2", "75" },
		/*4*/{ "09", "83", "2C", "1A", "1B", "6E", "5A", "A0", "52", "3B", "D6", "B3", "29", "E3", "2F", "84" },
		/*5*/{ "53", "D1", "00", "ED", "20", "FC", "B1", "5B", "6A", "CB", "BE", "39", "4A", "4C", "58", "CF" },
		/*6*/{ "D0", "EF", "AA", "FB", "43", "4D", "33", "85", "45", "F9", "02", "7F", "50", "3C", "9F", "A8" },
		/*7*/{ "51", "A3", "40", "8F", "92", "9D", "38", "F5", "BC", "B6", "DA", "21", "10", "FF", "F3", "D2" },
		/*8*/{ "CD", "0C", "13", "EC", "5F", "97", "44", "17", "C4", "A7", "7E", "3D", "64", "5D", "19", "73" },
		/*9*/{ "60", "81", "4F", "DC", "22", "2A", "90", "88", "46", "EE", "B8", "14", "DE", "5E", "0B", "DB" },
		/*a*/{ "E0", "32", "3A", "0A", "49", "06", "24", "5C", "C2", "D3", "AC", "62", "91", "95", "E4", "79" },
		/*b*/{ "E7", "C8", "37", "6D", "8D", "D5", "4E", "A9", "6C", "56", "F4", "EA", "65", "7A", "AE", "08" },
		/*c*/{ "BA", "78", "25", "2E", "1C", "A6", "B4", "C6", "E8", "DD", "74", "1F", "4B", "BD", "8B", "8A" },
		/*d*/{ "70", "3E", "B5", "66", "48", "03", "F6", "0E", "61", "35", "57", "B9", "86", "C1", "1D", "9E" },
		/*e*/{ "E1", "F8", "98", "11", "69", "D9", "8E", "94", "9B", "1E", "87", "E9", "CE", "55", "28", "DF" },
		/*f*/{ "8C", "A1", "89", "0D", "BF", "E6", "42", "68", "41", "99", "2D", "0F", "B0", "54", "BB", "16" }
	};// S盒
    
char Sboxni[16][16][3] = {
		/****   0     1     2     3     4     5     6     7     8     9     a     b     c     d     e     f */
		/*0*/{"52", "09", "6A", "D5", "30", "36", "A5", "38", "BF", "40", "A3", "9E", "81", "F3", "D7", "FB"},
		/*1*/{"7C", "E3", "39", "82", "9B", "2F", "FF", "87", "34", "8E", "43", "44", "C4", "DE", "E9", "CB"},
		/*2*/{"54", "7B", "94", "32", "A6", "C2", "23", "3D", "EE", "4C", "95", "0B", "42", "FA", "C3", "4E"},
		/*3*/{"08", "2E", "A1", "66", "28", "D9", "24", "B2", "76", "5B", "A2", "49", "6D", "8B", "D1", "25"},
		/*4*/{"72", "F8", "F6", "64", "86", "68", "98", "16", "D4", "A4", "5C", "CC", "5D", "65", "B6", "92"},
		/*5*/{"6C", "70", "48", "50", "FD", "ED", "B9", "DA", "5E", "15", "46", "57", "A7", "8D", "9D", "84"},
		/*6*/{"90", "D8", "AB", "00", "8C", "BC", "D3", "0A", "F7", "E4", "58", "05", "B8", "B3", "45", "06"},
		/*7*/{"D0", "2C", "1E", "8F", "CA", "3F", "0F", "02", "C1", "AF", "BD", "03", "01", "13", "8A", "6B"},
		/*8*/{"3A", "91", "11", "41", "4F", "67", "DC", "EA", "97", "F2", "CF", "CE", "F0", "B4", "E6", "73"},
		/*9*/{"96", "AC", "74", "22", "E7", "AD", "35", "85", "E2", "F9", "37", "E8", "1C", "75", "DF", "6E"},
		/*a*/{"47", "F1", "1A", "71", "1D", "29", "C5", "89", "6F", "B7", "62", "0E", "AA", "18", "BE", "1B"},
		/*b*/{"FC", "56", "3E", "4B", "C6", "D2", "79", "20", "9A", "DB", "C0", "FE", "78", "CD", "5A", "F4"},
		/*c*/{"1F", "DD", "A8", "33", "88", "07", "C7", "31", "B1", "12", "10", "59", "27", "80", "EC", "5F"},
		/*d*/{"60", "51", "7F", "A9", "19", "B5", "4A", "0D", "2D", "E5", "7A", "9F", "93", "C9", "9C", "EF"},
		/*e*/{"A0", "E0", "3B", "4D", "AE", "2A", "F5", "B0", "C8", "EB", "BB", "3C", "83", "53", "99", "61"},
		/*f*/{"17", "2B", "04", "7E", "BA", "77", "D6", "26", "E1", "69", "14", "63", "55", "21", "0C", "7D"}
	};	// S盒的逆

char Rcon[10][4][3] = {
	{"01", "00", "00", "00"},
	{"02", "00", "00", "00"},
	{"04", "00", "00", "00"},
	{"08", "00", "00", "00"},
	{"10", "00", "00", "00"},
	{"20", "00", "00", "00"},
	{"40", "00", "00", "00"},
	{"80", "00", "00", "00"},
	{"1B", "00", "00", "00"},
	{"36", "00", "00", "00"}
};  // 轮常量矩阵

static int flag_e = 00; //加密循环轮数
static int flag_d = 10; //解密循环轮数

vector<string> MixC = {"02", "01", "01", "03"};
vector<string> MixCni = {"0E", "09", "0D", "0B"};

string encrypt(string p, string k);
string decrypt(string c, string k);
string LowToUpper(string low);  //小写字母转大写字母
string HextoBin(string Hex);  //十六进制转二进制
string BintoHex(string Bin);  //二进制转十六进制
int HexToDec(char Hex);   //十六进制字符转10进制数字
vector<vector<string>> nineRounds(vector<vector<string>> P, vector<vector<string>> K, char ed);   //9轮变换
vector<vector<string>> finalRounds(vector<vector<string>> P, vector<vector<string>> K, char ed);   //最终轮变换
vector<vector<string>> subBytes(vector<vector<string>> P, char ed); // 字节代换
vector<vector<string>> ShiftRows(vector<vector<string>> P);   // 行移位
vector<vector<string>> invShiftRows(vector<vector<string>> P);   // 逆行移位
vector<vector<string>> MixColumns(vector<vector<string>> p, vector<string> MisC);   //列混合
vector<vector<string>> AddRoundKey(vector<vector<string>> P, vector<vector<string>> K, char ed);  //轮密钥加
vector<vector<string>> subKey(vector<vector<string>> k);  //密钥扩展
string hexXor(string a, string b); //16进制抑或
string hexTimes(string a, string b);  //16进制乘法，a为列混合矩阵元素,只取01,02,03
vector<vector<string>> transMatrix(vector<vector<string>> K);   // 转置矩阵

string encrypt(string p, string k) {
	vector<vector<string>> P(4, vector<string>(4, "00"));
	vector<vector<string>> Key(44, vector<string>(4, "00"));
	string ans = "";
	for (int i = 0; i < 4; i++) {
		for (int j = 0; j < 4; j++) {
			for(int kk = 0; kk < 2; kk++) {
				P[i][j][kk] = p[(i * 4 + j) * 3 + kk];   //丢弃掉中间的空格，将字符串转换为矩阵
			}
		}
	}
	for (int i = 0; i < 4; i++) {
		for (int j = 0; j < 4; j++) {
			for(int kk = 0; kk < 2; kk++) {
				Key[i][j][kk] = k[(i * 4 + j) * 3 + kk];   //丢弃掉中间的空格，将字符串转换为矩阵
			}
		}
	}
	Key = subKey(Key);
	for (int i = 0; i < 4; i++) {
		for (int j = 0; j < 4; j++) {
			P[i][j] = hexXor(P[i][j], Key[i][j]);   //初始变换(Initial round)
		}
	}
	P = transMatrix(P);   // 明文矩阵是竖着读进去的，但是需要横着用
	P = nineRounds(P, Key, 'e');   //9轮循环运算
	P = finalRounds(P, Key, 'e');  //最终轮运算
	P = transMatrix(P);  //还原回去
	for (int i = 0; i < 4; i++) {
		for (int j = 0; j < 4; j++) {
			ans += P[i][j];
			ans += " ";
		}
	}
	return ans;    // 得到密文
}

string decrypt(string c, string k) {
	vector<vector<string>> C(4, vector<string>(4, "00"));
	vector<vector<string>> Key(44, vector<string>(4, "00"));
	string ans = "";
	for (int i = 0; i < 4; i++) {
		for (int j = 0; j < 4; j++) {
			for(int kk = 0; kk < 2; kk++) {
				C[i][j][kk] = c[(i * 4 + j) * 3 + kk];   //丢弃掉中间的空格，将字符串转换为矩阵
			}
		}
	}
	for (int i = 0; i < 4; i++) {
		for (int j = 0; j < 4; j++) {
			for(int kk = 0; kk < 2; kk++) {
				Key[i][j][kk] = k[(i * 4 + j) * 3 + kk];   //丢弃掉中间的空格，将字符串转换为矩阵
			}
		}
	}
	Key = subKey(Key);
	for (int i = 0; i < 4; i++) {
		for (int j = 0; j < 4; j++) {
			C[i][j] = hexXor(C[i][j], Key[40 + i][j]);   //逆初始变换(Initial round)
		}
	}
	C = transMatrix(C);   // 密文矩阵是竖着读进去的，但是需要横着用
	C = finalRounds(C, Key, 'd');  //最终轮运算
	C = nineRounds(C, Key, 'd');   //9轮循环运算
	C = transMatrix(C); //还原回去
	for (int i = 0; i < 4; i++) {
		for (int j = 0; j < 4; j++) {
			ans += C[i][j];
			ans += " ";
		}
	}
	return ans;    // 得到密文
}

vector<vector<string>> subKey(vector<vector<string>> K) {  //密钥扩展
	vector<vector<string>> sKey(44, vector<string>(4, "00"));
	for (int i = 0; i < 4; i++) {
		for (int j = 0; j < 4; j++) {
			sKey[i][j] = K[i][j];
		}
	}
	for (int i = 4; i < 44; i++) {
		if (i % 4 != 0) {
			for (int j = 0; j < 4; j++) {
				sKey[i][j] = hexXor(sKey[i - 4][j], sKey[i - 1][j]);
			}
		}
		else {
			vector<string> tempK = sKey[i - 1];  //字循环
			string temp = tempK[0];
			for (int j = 0; j < 3; j++) {
				tempK[j] = tempK[j + 1];
			}
			tempK[3] = temp;
			vector<string> SK = tempK;           //S盒
			for (int j = 0; j < 4; j++) {
				SK[j] = Sbox[HexToDec(tempK[j][0])][HexToDec(tempK[j][1])];
				sKey[i][j] = hexXor(hexXor(sKey[i - 4][j], SK[j]), Rcon[i / 4 - 1][j]);
			}
		}
	}
	return sKey;
}

vector<vector<string>> subBytes(vector<vector<string>> P, char ed) { // 字节代换
	vector<vector<string>> SP(4, vector<string>(4, "00"));
	if (ed == 'e') {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				SP[i][j] = Sbox[HexToDec(P[i][j][0])][HexToDec(P[i][j][1])];  //字节代换(SubBytes)
			}
		}
	}
	else {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				SP[i][j] = Sboxni[HexToDec(P[i][j][0])][HexToDec(P[i][j][1])];  //字节代换(SubBytes)
		}
		}
	}
	return SP;
}

vector<vector<string>> ShiftRows(vector<vector<string>> P) {  // 行移位(ShiftRows)
	for (int i = 1; i < 4; i++) {
		vector<string> temp;
		for (int j = 0; j < i; j++) {
			temp.push_back(P[i][j]);
		}
		for (int j = i; j < 4; j++) {
			P[i][j - i] = P[i][j];
		}
		for (int j = 4 - i; j < 4; j++) {
			P[i][j] = temp[j - (4 - i)];
		}
	}
	return P;
}

vector<vector<string>> invShiftRows(vector<vector<string>> P) {   // 逆行移位
	for (int i = 1; i < 4; i++) {
		vector<string> temp;
		for (int j = 4 - i; j < 4; j++) {
			temp.push_back(P[i][j]);
		}
		for (int j = 3; j >= i; j--) {
			P[i][j] = P[i][j - i];
		}
		for (int j = 0; j < i; j++) {
			P[i][j] = temp[j];
		}
	}
	return P;
}

vector<vector<string>> MixColumns(vector<vector<string>> p, vector<string> A) {   //列混合
	p = transMatrix(p);  //将列转化为行，方便计算
	vector<vector<string>> SP(4, vector<string>(4, "00"));	
	for (int i = 0; i < 4; i++) {  // GF(2^8)上的矩阵乘法
			SP[i][0] = hexXor(hexXor(hexTimes(A[0], p[i][0]), hexTimes(A[3], p[i][1])), hexXor(hexTimes(A[2], p[i][2]), hexTimes(A[1], p[i][3])));
			SP[i][1] = hexXor(hexXor(hexTimes(A[1], p[i][0]), hexTimes(A[0], p[i][1])), hexXor(hexTimes(A[3], p[i][2]), hexTimes(A[2], p[i][3])));
			SP[i][2] = hexXor(hexXor(hexTimes(A[2], p[i][0]), hexTimes(A[1], p[i][1])), hexXor(hexTimes(A[0], p[i][2]), hexTimes(A[3], p[i][3])));
			SP[i][3] = hexXor(hexXor(hexTimes(A[3], p[i][0]), hexTimes(A[2], p[i][1])), hexXor(hexTimes(A[1], p[i][2]), hexTimes(A[0], p[i][3])));
	}
	SP = transMatrix(SP);  //再转化回去
	return SP;
}

vector<vector<string>> AddRoundKey(vector<vector<string>> P, vector<vector<string>> K, char ed) {  //轮密钥加
	P = transMatrix(P);   //轮密钥加要用列，先翻转一下
	vector<vector<string>> ARK(4, vector<string>(4, "00"));
	if (ed == 'e') {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				ARK[i][j] = hexXor(P[i][j], K[flag_e * 4 + i][j]);
			}
		}
	}
	else {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				ARK[i][j] = hexXor(P[i][j], K[flag_d * 4 + i][j]);
			}
		}
	}
	ARK = transMatrix(ARK);   //再给他翻转回去
	return ARK;
}

vector<vector<string>> nineRounds(vector<vector<string>> P, vector<vector<string>> K, char ed) {  // 9轮变换
	if (ed == 'e') {
		for (int i = 0; i < 9; i++) {
			flag_e++;
			P = subBytes(P, 'e');
			P = ShiftRows(P);
			P = MixColumns(P, MixC);
			P = AddRoundKey(P, K, 'e');
		}
	}
	else {
		for (int i = 0; i < 9; i++) {
			flag_d--;
			P = MixColumns(P, MixCni);
			P = invShiftRows(P);
			P = subBytes(P, 'd');
			P = AddRoundKey(P, K, 'd');
		}
	}
	return P;
}

vector<vector<string>> finalRounds(vector<vector<string>> P, vector<vector<string>> K, char ed) {   //最终轮变换
	if (ed == 'e') {
		flag_e++;
		P = subBytes(P, 'e');
		P = ShiftRows(P);
		P = AddRoundKey(P, K, 'e');
	}
	else {
		flag_d--;
		P = invShiftRows(P);
		P = subBytes(P, 'd');
		P = AddRoundKey(P, K, 'd');
	}
	return P;
}

string HextoBin(string Hex) {
    string Bin = "";
    for(int i = 0; i < Hex.length(); i++) {
        if(Hex[i] == '0') Bin += "0000";
        else if(Hex[i] == '1') Bin += "0001";
        else if(Hex[i] == '2') Bin += "0010";
        else if(Hex[i] == '3') Bin += "0011";
        else if(Hex[i] == '4') Bin += "0100";
        else if(Hex[i] == '5') Bin += "0101";
        else if(Hex[i] == '6') Bin += "0110";
        else if(Hex[i] == '7') Bin += "0111";
        else if(Hex[i] == '8') Bin += "1000";
        else if(Hex[i] == '9') Bin += "1001";
        else if(Hex[i] == 'A') Bin += "1010";
        else if(Hex[i] == 'B') Bin += "1011";
        else if(Hex[i] == 'C') Bin += "1100";
        else if(Hex[i] == 'D') Bin += "1101";
        else if(Hex[i] == 'E') Bin += "1110";
        else Bin += "1111";
    }
    return Bin;
}

string BintoHex(string Bin) {
    string Hex = "";
    for(int i = 0; i < Bin.length() / 4; i++) {
        int num = (Bin[4 * i] - '0') * 8 + (Bin[4 * i + 1] - '0') * 4 + (Bin[4 * i + 2] - '0') * 2 + (Bin[4 * i + 3] - '0');
        if(0 <= num && num <= 9) Hex += (num + '0');
        else if(num == 10) Hex += "A";
        else if(num == 11) Hex += "B";
        else if(num == 12) Hex += "C";
        else if(num == 13) Hex += "D";
        else if(num == 14) Hex += "E";
        else Hex += "F";
    }
    return Hex;
}

string LowToUpper(string low) {
    for(int i = 0; i < low.length(); i++) {
        if(low[i] >= 'a' && low[i] <= 'z') {
            low[i] -= 32;
        }
    }
    return low;
}

int HexToDec(char Hex) {
	if(Hex >= '0' && Hex <= '9')
		return Hex - '0';
	else 
		return Hex - 55;
}

string hexXor(string a, string b) {
	string A = HextoBin(a);
	string B = HextoBin(b);
	string x(A.length(), '0');
	for (int i = 0; i < A.length(); i++) {
		x[i] = (A[i] - '0') ^ (B[i] - '0') + '0';
	}
	return BintoHex(x);
}

string hexTimes(string a, string b) {
	string A = HextoBin(a);
	string B = HextoBin(b);
	string ans(8, '0');
	if (a == "01") {
		return b;
	}
	else if (a == "02") {
		for (int i = 0; i < 7; i++) {
			ans[i] = B[i + 1];
		}
		if (B[0] == '0') {
			return BintoHex(ans);
		}
		else {
			return hexXor(BintoHex(ans), "1B");
		}
	}
	else if (a == "03") {
		return hexXor(hexTimes("02", b), b);
	}
	else if (a == "04") {
		return hexTimes("02", hexTimes("02", b));
	}
    else if (a == "08") {
        return hexTimes("02", hexTimes("04", b));
    }
    else if (a == "09") {
        return hexXor(b, hexTimes("08", b));
    }
    else if (a == "0E") {
        return hexXor(hexTimes("08", b), hexXor(hexTimes("04", b), hexTimes("02", b)));
    }
    else if (a == "0D") {
        return hexXor(hexTimes("09", b), hexTimes("04", b));
    }
    else if (a == "0B") {
        return hexXor(hexTimes("09", b), hexTimes("02", b));
    }
	else {
		cout << "列混合步骤出错，用到了不支持的矩阵乘法，请检查列混合矩阵！" << endl;
		system("pause");
		return 0;
	}
}

vector<vector<string>> transMatrix(vector<vector<string>> K) {
	vector<vector<string>> TK(K[0].size(), vector<string>(K.size(), "00"));
	for (int i =0; i < K.size(); i++) {
		for (int j = 0; j < K[0].size(); j++) {
			TK[j][i] = K[i][j];
		}
	}
	return TK;
}

int main(int argc, char** argv) {
	string PATH;
    string p, c, k;   //明文、密文、密钥
	cout << "-------------------AES-128加/解密工具--------------------" << endl << endl;
    cout << "请输入你想加密还是解密，加密为e，解密为d：";
    string ed;
    cin >> ed;
	cout << "\n请输入文件路径：";
	cin >> PATH;
	fstream File(PATH, ios::out|ios::in);   // 以读取和覆盖的方式打开文件
	if(!File.is_open()){
        cout << "\n打开或创建文件失败，请检查文件是否被占用！\n" << endl;
        system("pause");
        return 0;
    }
	vector<string> textBox;
	while(!File.eof()) {
		string temp;
		getline(File, temp, '\n');
		textBox.push_back(temp);
	}
	File.seekg(0, ios::beg);  // 回到文件头
	if (textBox.size() == 2) {   // 文件中只有明文和密钥
		p = textBox[0];
		k = textBox[1];
		textBox.push_back("");
	}
	else {                       // 文件中有明文、密钥和密文
		p = textBox[0];
		k = textBox[1];
		c = textBox[2];
	}
	k = LowToUpper(k);
	p = LowToUpper(p);
	c = LowToUpper(c);  // 将读入字符全部变为大写，增加健壮性
	if (ed == "e" || ed == "E") {
		c = encrypt(p, k);
		textBox[2] = c;
		for (int i = 0; i < textBox.size(); i++) {
			File << textBox[i];
			if (i != textBox.size() - 1) {
				File << endl;
			}
		}
		cout << "加密成功，密文已保存至源文件最后一行，请到源文件查看！" << endl;
		File.close();   // 及时关闭文件，防止内存泄露
	}
	else if (ed == "d" || ed == "D") {
		p = decrypt(c, k);
		textBox[0] = p;
		for (int i = 0; i < textBox.size(); i++) {
			File << textBox[i];
			if (i != textBox.size() - 1) {
				File << endl;
			}
		}
		cout << "解密成功，明文已保存至源文件第一行，请到源文件查看！" << endl;
		File.close();
	}
	else {
		cout << "参数错误！\n" << endl;
		File.close();  // Close the file
		system("pause");
		return 0;
	}
	system("pause");
	return 0;
}

