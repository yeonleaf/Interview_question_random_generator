import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

public class Main {

    private static Scanner sc = new Scanner(System.in);
    private static final String DEFAULT_PATH = "C:\\Users\\조주연\\Desktop\\irg\\src\\txt\\";


    private static List<String> borrow_data(String name) throws IOException {
        Path path = Paths.get(DEFAULT_PATH + name + ".txt");
        return Files.readAllLines(path);
    }

    private static boolean check_name(String name) {
        for (int i=0; i<name.length(); i++) {
            if (String.valueOf(name.charAt(i)).matches("[^a-z0-9_]")) return false;
        }
        return true;
    }

    private static void cmd_new_category() throws IOException {
        // names.txt 파일이 있는지 확인하고 없으면 새로 만들기
        Path names_path = Paths.get(DEFAULT_PATH + "names.txt");
        if (!Files.exists(names_path)) {
            Files.createFile(names_path);
        }

        String fileName;
        System.out.println("새로 생성할 파일의 이름을 입력해주세요. _ 외의 특수문자는 불가하며 영문 소문자와 숫자만 사용할 수 있습니다.");
        System.out.println("파일 생성을 종료하려면 -1을 입력해주세요");
        while (true) {
            fileName = sc.next();
            if (fileName.equals("-1")) {
                System.out.println("파일 생성을 종료하고 메인으로 돌아갑니다.");
                return;
            }
            if (check_name(fileName)) {
                Path file_path = Paths.get(DEFAULT_PATH + fileName + ".txt");
                if (Files.exists(file_path)) {
                    System.out.println("이미 같은 이름의 파일이 있습니다. 메인으로 돌아가려면 0, 취소하고 다른 이름을 입력하려면 1을 입력해주세요.");
                    String order = sc.next();
                    if (order == "0") {
                        System.out.println("카테고리 생성을 취소하고 메인으로 돌아갑니다.");
                        return;
                    }
                } else {
                    Files.createFile(file_path);
                    BufferedWriter writer = new BufferedWriter(new FileWriter(names_path.toFile(), true));
                    writer.write(fileName);
                    writer.newLine();
                    writer.flush();
                    writer.close();
                    System.out.println("카테고리가 정상적으로 생성되었습니다.");
                }
            } else {
                System.out.println("잘못된 파일명입니다. 파일명에 _ 외의 특수문자는 불가하며 영문 소문자와 숫자만 사용할 수 있습니다.");
            }
        }
    }

    private static void print_names(List<String> names) {
        for (int i=0; i<names.size(); i++) {
            System.out.println(i + " : " + names.get(i));
        }
    }
    private static void cmd_new_question() throws IOException {

        Path names_path = Paths.get(DEFAULT_PATH + "names.txt");
        if (!Files.exists(names_path)) {
            Files.createFile(names_path);
        }

        List<String> names;
        try {
            names = borrow_data("names");

            if (names.isEmpty()) {
                System.out.println("파일이 하나도 없습니다. 파일 추가 모드로 들어가서 파일을 추가해주세요.");
                System.out.println("메인으로 돌아갑니다.");
                return;
            }

            System.out.println("질문을 추가할 파일명을 입력해주세요. 번호로 입력해주셔야 합니다.");
            System.out.println("질문 추가를 종료하고 메인으로 돌아가려면 -1을 입력해주세요.");
            print_names(names);

            String fileNum;
            while (true) {
                fileNum = sc.nextLine();
                if (fileNum == "-1") {
                    System.out.println("질문 추가를 종료하고 메인 화면으로 돌아갑니다.");
                    return;
                }
                try {
                    int fn = Integer.parseInt(fileNum);
                    if (0 <= fn && fn <= names.size()-1) {
                        Path file_path = Paths.get(DEFAULT_PATH + names.get(fn) + ".txt");
                        if (!Files.exists(file_path)) {
                            System.out.println("파일이 없습니다. 새로 생성합니다.");
                            Files.createFile(file_path);
                        }

                        System.out.println("질문을 입력해주세요.");
                        System.out.println("질문 추가를 종료하고 메인으로 돌아가려면 -1을 입력해주세요.");
                        while (true) {
                            String question = sc.nextLine();
                            if (question.equals("-1")) {
                                System.out.println("질문 추가를 종료하고 메인으로 돌아갑니다.");
                                return;
                            }
                            BufferedWriter writer = new BufferedWriter(new FileWriter(file_path.toFile(), true));
                            writer.write(question);
                            writer.newLine();
                            writer.flush();
                            writer.close();
                        }
                    } else {
                        System.out.println("잘못된 파일 번호입니다. 제대로 된 파일 번호를 입력해주세요.");
                        print_names(names);
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("입력한 값이 숫자가 아닙니다. 다시 입력해 주세요.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            exit(0);
        }


        //
        /*
        * names에서 파일 목록을 가져온다.
        * 파일 목록을 인덱스 번호와 함께 리스트 형태로 보여준다.
        * while
        * fileIdx 어떤 파일에 질문을 추가할 것인지 입력받기
        * fileIdx를 제대로 받은 경우 (인덱스 범위 내에 있어야 함)
        *   파일이 있는지 확인한다.
        *       해당 파일이 없으면 경고문을 띄운 뒤 파일을 새로 생성한 후 break.
        *
        * 질문을 입력해주세요. 질문을 더 이상 입력하고 싶지 않다면 -1을 입력해주세요.
        * writer 열기
        * while
        *   String q = sc.next() == "-1" -> 질문 입력을 종료합니다. return;
        *   질문을 파일에 쓰기
        * writer 닫기
        * */
    }



    private static void test() {
        //
    }
    public static void main(String[] args) {
//        try {
//            cmd_new_category();
//        } catch (IOException e) {
//            e.printStackTrace();
//            exit(0);
//        }

        try {
            cmd_new_question();
        } catch (IOException e) {
            e.printStackTrace();
            exit(0);
        }
    }
}