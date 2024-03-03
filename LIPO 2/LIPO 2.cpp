#include <iostream>
#include <fstream>
#include <locale>
#include <cctype>
#include <string>
#include <cmath>
#include <vector>
#include <algorithm>
using namespace std;

int findd(vector <string> v, string ser) {
	if (any_of(v.begin(), v.end(), [&](const std::string& elem) { return elem == ser; }))
	{
		return 1;
	}
	else {
		return -1;
	}
}

struct lexem {
	string name;
	int type;
	int line;
}lex;

fstream fi, fo;
int count_error = 0;
vector<string> ident;

void read()
{
	fi >> lex.name >> lex.type >> lex.line;
}

void declaration_of_variables()
{
	int state = 0;
	bool flag1 = true;
	while (flag1 != false) {
		if (state == 0) {
			if (lex.type == 11)/*VAR*/ {
				state = 1;
				read();
			}
			else {
				cout << "Ожидалось ( Var ), но найдено { " << lex.name << " } номер строки : " << lex.line << endl;
				count_error++;
			}
		}
		else if (state == 1) {
			if (lex.type == 2)/* ident */ {
				ident.push_back(lex.name);
				state = 2;
				read();
			}
			else {
				cout << "Ожидалось ( идентификатор ), но найдено { " << lex.name << " } номер строки : " << lex.line << endl;
				count_error++;
				read();
				state = 2;
			}
		}
		else if (state == 2) {
			if (lex.type == 8 && lex.name[0] == ',')/*<,>*/ {
				state = 1;
				read();
			}
			else if (lex.type == 8 && lex.name[0] == ';')/*<;>*/ {
				cout << "Обьявление переменных проверено" << endl;
				cout << "Количество ошибок: " << count_error << endl;
				flag1 = false;
				read();
			}
			else if (lex.type == 12) {
				cout << "Ожидалось ( ; ), но найдено { " << lex.name << " } номер строки : " << lex.line << endl;
				count_error++;
				flag1 = false;
			}
			else
			{
				cout << "Ожидалось ( , ), но найдено { " << lex.name << " } номер строки : " << lex.line << endl;
				count_error++;
				state = 1;
			}
		}
	}
}

void description_of_the_calculations()
{
	int state = 0;
	int l = 0, r = 0; //l- ( , r - );
	bool flag1 = true;
	while (flag1) {
		if (state == 0) {
			if (lex.type == 12)/*Begin*/ {

				state = 1;
				read();
			}
			else {
				cout << "Error : ожидалось ( Begin ), но найдено { " << lex.name << " }, номер строки: " << lex.line << endl;
				state = 1;
				count_error++;
			}
		}
		else if (state == 1) {
			if (lex.type == 2)/* ident */ {
				state = 2;
				read();
			}
			else if (lex.type == 13)/*END*/ {
				if (r < l)
				{
					cout << "Error : отсутствует закрывающая скобка, номер строки: " << lex.line << endl;
					state = 1;
					r++;
					count_error++;
				}
				else if (l < r)
				{
					cout << "Error : отсутствует открывающая скобка, номер строки: " << lex.line << endl;
					state = 1;
					l++;
					count_error++;
				}
				else
				{
					cout << "Описание вычислений проверено" << endl;
					cout << "Количество ошибок: " << count_error << endl;
					flag1 = false;
				}
			}
			else {
				cout << "Error : ожидалось ( операнд ), но найдено { " << lex.name << " }, номер строки: " << lex.line << endl;
				read();
				count_error++;
				state = 2;
			}
		}
		else if (state == 2) {
			if (lex.type == 4)/*< = >*/ {
				state = 3;
				read();
			}
			else {
				cout << "Error : ожидалось ( = ), но найдено { " << lex.name << " }, номер строки: " << lex.line << endl;
				read();
				count_error++;
				state = 3;
			}
		}
		else if (state == 3) {     /* test for vyrajenie */
			if (lex.type == 7)/* [-]*/ {
				state = 5;
				read();
			}
			else {
				state = 5;
			}
		}
		else if (state == 4) {/* binary operations */
			if (lex.type == 8)/* < ; >*/ {
				state = 1;
				while (l != r)
				{
					if (r < l) {
						r++;
						cout << "Error : отсутствует закрывающая скобка, номер строки: " << lex.line << endl;
						count_error++;
					}
					if (l < r) {
						l++;
						cout << "Error : отсутствует открывающая скобка, номер строки: " << lex.line << endl;
						count_error++;
					}
				}
				read();
			}
			else if (lex.type == 5 || lex.type == 6)
			{
				state = 5;
				read();
			}
			else if (lex.type == 10)/* < ) >*/ {
				r++;
				state = 4;
				read();
			}
			else if (lex.type == 13) /* End */ {
				while (l != r)
				{
					if (r < l) {
						r++;
						cout << "Error : отсутствует закрывающая скобка, номер строки: " << lex.line - 1 << endl;
						count_error++;
					}
					if (l < r) {
						l++;
						cout << "Error : отсутствует открывающая скобка, номер строки: " << lex.line - 1 << endl;
						count_error++;
					}
				}
				cout << "Error : ожидалось ( ; ), но найдено { " << lex.name << " }, номер строки: " << lex.line << endl;
				count_error++;
				state = 1;
			}
			else if (lex.type == 2)
			{
				cout << "Error : ожидалось ( ; || bin ), но найдено { " << lex.name << " }, номер строки: " << lex.line << endl;
				count_error++;
				read();
				if (lex.type == 4)
				{
					state = 3;
					read();
				}
				else {
					state = 4;
				}
			}
			else {
				cout << "Error : ожидалось ( bin ), но найдено { " << lex.name << " }, номер строки: " << lex.line << endl;
				count_error++;
				state = 4;
				read();
			}
		}
		else if (state == 5) {
			if (lex.type == 9)/* < ( >*/ {
				l++;
				state = 3;
				read();
			}
			else if (lex.type == 10)/* < ) >*/ {
				r++;
				state = 4;
				read();
			}
			else if (lex.type == 2 || lex.type == 3) {
				state = 4;
				read();
			}
			else {
				cout << "Error : ожидалось ( операнд ), но найдено { " << lex.name << " }, номер строки: " << lex.line << endl;
				count_error++;
				state = 4;
			}
		}
	}
}

int main()
{
	setlocale(0, "rus");
	fi.open("C:\\21VA1\\4 term\\LIPO\\LIPO 1\\lixem.txt");
	if (!fi.is_open())
	{
		cout << "No file!";
		exit(2);
	}
	read();
	declaration_of_variables();
	description_of_the_calculations();
	cout << endl;
	system("pause");
	return 0;
}