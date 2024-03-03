#include <iostream>
#include <string>
#include <regex>
#include <fstream>
#include <assert.h>
using namespace std;

int main() {
	string str;
	ifstream fin("input.txt");
	ofstream fo("lixem.txt");
	if (!fin.is_open() && !fo.is_open())
	{
		cout << "error! file not is open!" << endl;
	}
	else
	{
		cout << "file is open!" << endl;
		cmatch result;		
		const regex reg("(\\w+)"), abc("([a-zA-Z]+)"), digit("([0-9]+)"), symbol("(\\W)");
		const string key[3] = { "Var", "Begin", "End" };

		int j = 1;
		char ch, unar = ' ';
		string buff;
		while (fin.get(ch))
		{
			str = ch;
			if (regex_match(str.c_str(), result, reg))
			{
				buff += ch;
			}			
			else if (regex_match(buff.c_str(), result, abc))
			{
				int i = 0;
				for (int k = 0; k < 3; k++)
				{
					if (buff == key[k])
					{
						cout << buff << "\t" << k + 11 << "\t" << j << endl;
						fo << buff << "\t" << k + 11 << "\t" << j << endl;
						i = 1;
					}
				}
				if (!i)
				{
					cout << buff << "\t" << 2 << "\t" << j << endl;
					fo << buff << "\t" << 2 << "\t" << j << endl;
				}
				buff = "";
				unar = ' ';
			}
			else if (regex_match(buff.c_str(), result, digit))
			{
				cout << buff << "\t" << 3 << "\t" << j << endl;
				fo << buff << "\t" << 3 << "\t" << j << endl;
				buff = "";
				unar = ' ';
			}
			else if (regex_match(buff.c_str(), result, reg))
			{
				cout << buff << "\t" << 10 << "\t" << j << endl;
				fo << buff << "\t" << 10 << "\t" << j << endl;
				buff = "";
			}
			ch == '\n' ? j++ : j = j;
			if (regex_search(str.c_str(), result, symbol))
			{
				switch (ch)
				{
				case ',':
					unar = ' ';
					cout << ch << "\t" << 8 << "\t" << j << endl;
					fo << ch << "\t" << 8 << "\t" << j << endl;
					break;
				case '+':
					cout << ch << "\t" << 5 << "\t" << j << endl;
					fo << ch << "\t" << 5 << "\t" << j << endl;
					unar = ' ';
					break;
				case '-':
					if (unar == '=' || unar == '(')
					{
						cout << ch << "\t" << 7 << "\t" << j << endl;
						fo << ch << "\t" << 7 << "\t" << j << endl;
						unar = ' ';
					}
					else
					{
						cout << ch << "\t" << 5 << "\t" << j << endl;
						fo << ch << "\t" << 5 << "\t" << j << endl;
					}
					break;
				case '/':
					cout << ch << "\t" << 6 << "\t" << j << endl;
					fo << ch << "\t" << 6 << "\t" << j << endl;
					unar = ' ';
					break;
				case '*':
					cout << ch << "\t" << 6 << "\t" << j << endl;
					fo << ch << "\t" << 6 << "\t" << j << endl;
					unar = ' ';
					break;
				case '=':
					unar = ch;
					cout << ch << "\t" << 4 << "\t" << j << endl;
					fo << ch << "\t" << 4 << "\t" << j << endl;
					break;
				case ';':
					cout << ch << "\t" << 8 << "\t" << j << endl;
					fo << ch << "\t" << 8 << "\t" << j << endl;
					unar = ' ';
					break;
				case '(':
					unar = ch;
					cout << ch << "\t" << 9 << "\t" << j << endl;
					fo << ch << "\t" << 9 << "\t" << j << endl;
					break;
				case ')':
					cout << ch << "\t" << 10 << "\t" << j << endl;
					fo << ch << "\t" << 10 << "\t" << j << endl;
					unar = ' ';
					break;
				default:
					if (unar == '=' || unar == '(') {}
					else{ unar = ' ';}
					break;
				}				
			}
		}
	}
	fin.close();
	fo.close();
}