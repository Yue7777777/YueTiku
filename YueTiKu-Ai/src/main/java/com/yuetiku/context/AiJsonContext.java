package com.yuetiku.context;

public class AiJsonContext {
    public static final String CONTEXT ="""
            请你从用户给出的题目内容中提取出题目信息并按照指定的JSON Schema格式输出，输出格式要求如下：
            [
            {
                "type"： 题目的类型，只能是single,multiple,fill,answer,judge中的一个
                "title"： 题目的标题，如果没有就填相应题型的中文,如single对应单选题
                "content"： 题目的问题部分,选择题不包括选项
                "explanation"： 题目的解析部分，只有当文档中没有给出对应题目的解析的时候你就可以自己根据题目的问题部分给出解析
                "difficulty" 题目的难度,必需是easy,medium,hard中的一个，如果没有就默认medium
                "options": [
                   {
                     "optionKey":  选项，如"A"
                     "optionContent": 选项的具体内容
                     "isCorrect": 该选项是否正确，只能的true或者false中的一个
                     "sortOrder": 选项的排列顺序
                   },
                   ......
                 ],
                 "answer": {
                   "answerType": 正确答案的类型，只能选择option或者text中的一个
                   "correctAnswer": 标准答案的内容，text类型,如"AB","true","extends"
                   "answerExplanation": 答案的解释，如"extends关键字用于实现类的继承"，只有当文档中没有给出为什么选择这个选项或者填相应内容的时候你可以根据正确答案给出解释
                 }
               },
               ......
               ]
              然后这是具体例子，比如用户给出以下文本：
               1.题目：一个书包的单价是 35 元，买 2 个这样的书包，一共需要花多少元？（ ）
               A. 70 B. 37 C. 60 D. 175
                 答案：A
                 解析：根据 “总价 = 单价 × 数量”，代入数据计算得 35×2=70（元），故选 A。
               2.题目：下面属于 20 以内质数的有（ ）
               A. 11 B. 13 C. 15 D. 1
                 答案：AB
                 解析：质数是指 “只有 1 和它本身两个因数的数”。15 的因数有 1、3、5、15（合数），1 既不是质数也不是合数，因此排除 C、D，选 AB。
               3.题目：计算 19÷4，商是（ ），余数是（ ）。
                 答案：4；3
                 解析：根据除法运算，4×4=16，19-16=3，且余数 3＜除数 4，符合 “余数小于除数” 的规则，故商为 4，余数为 3。
               4.正方形是特殊的长方形。（ ）
                 答案：√
                 解析：长方形的定义是 “对边相等、四个角都是直角的四边形”，正方形满足 “对边相等、四个角是直角”，且额外有 “四条边都相等”，因此是特殊的长方形。
               5.把 1 米 5 分米换算成以 “米” 为单位的数，结果是多少？。
                 答案：结果是 1.5 米。
                 解析：长度单位中，1 分米 = 0.1 米，因此 5 分米 = 5×0.1=0.5 米；再加上原本的 1 米，可得 1 米 + 0.5 米 = 1.5 米。
               根据这段信息，转换得到的JSON是：
               [
               {
                "type": "single",
                 "title": "单选题",
                 "content": "一个书包的单价是 35 元，买 2 个这样的书包，一共需要花多少元？（ ）",
                 "explanation": "总价 = 单价 × 数量，35×2=70（元），故选 A。",
                 "difficulty": "medium",
                 "options": [
                  { "optionKey": "A", "optionContent": "70", "isCorrect": true,  "sortOrder": 1 },
                  { "optionKey": "B", "optionContent": "37", "isCorrect": false, "sortOrder": 2 },
                  { "optionKey": "C", "optionContent": "60", "isCorrect": false, "sortOrder": 3 },
                  { "optionKey": "D", "optionContent": "175","isCorrect": false, "sortOrder": 4 }
                 ],
                 "answer": {
                  "answerType": "option",
                  "correctAnswer": "A",
                  "answerExplanation": "35×2=70。"
                  }
                 },
                 {
                   "type": "multiple",
                   "title": "多选题",
                   "content": "下面属于 20 以内质数的有（ ）",
                   "explanation": "质数只有 1 和它本身两个因数。15 的因数有 1、3、5、15（合数）；1 既不是质数也不是合数；故选 A、B。",
                   "difficulty": "medium",
                   "options": [
                     { "optionKey": "A", "optionContent": "11", "isCorrect": true,  "sortOrder": 1 },
                     { "optionKey": "B", "optionContent": "13", "isCorrect": true,  "sortOrder": 2 },
                     { "optionKey": "C", "optionContent": "15", "isCorrect": false, "sortOrder": 3 },
                     { "optionKey": "D", "optionContent": "1",  "isCorrect": false, "sortOrder": 4 }
                   ],
                   "answer": {
                     "answerType": "option",
                     "correctAnswer": "AB",
                     "answerExplanation": "11、13 为质数；15 为合数；1 既非质数也非合数。"
                   }
                 },
                 {
                   "type": "fill",
                   "title": "填空题",
                   "content": "计算 19÷4，商是（ ），余数是（ ）。",
                   "explanation": "4×4=16，19-16=3，且余数 3＜除数 4，故商为 4，余数为 3。",
                   "difficulty": "medium",
                   "options": [],
                   "answer": {
                     "answerType": "text",
                     "correctAnswer": "4；3",
                     "answerExplanation": "商 4，余 3。"
                   }
                 }，
                 {
                   "type": "judge",
                   "title": "判断题",
                   "content": "正方形是特殊的长方形。（ ）",
                   "explanation": "长方形：对边相等、四角直角；正方形满足且四边相等，属于特殊的长方形。",
                   "difficulty": "medium",
                   "options": [],
                   "answer": {
                     "answerType": "text",
                     "correctAnswer": "true",
                     "answerExplanation": "命题正确。"
                   }
                 },
                 {
                   "type": "answer",
                   "title": "简答题",
                   "content": "把 1 米 5 分米换算成以“米”为单位的数，结果是多少？请简要说明换算过程。",
                   "explanation": "1 分米 = 0.1 米，因此 5 分米 = 0.5 米；1 米 + 0.5 米 = 1.5 米。",
                   "difficulty": "medium",
                   "options": [],
                   "answer": {
                     "answerType": "text",
                     "correctAnswer": "结果是1.5 米",
                     "answerExplanation": "1dm=0.1m，5dm=0.5m，合计 1.5m。"
                   }
                 }
               ]
            """;
    public static final String USER_TEXT= """
           你只需要将文档里的内容快速完整地提取出来，不需要做其它额外地工作
           """;
}
