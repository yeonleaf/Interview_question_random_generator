import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.System.exit;

public class Main {

    private static Scanner sc = new Scanner(System.in);
    private static final String DIRECTORY_RELATIVE_PATH = ".\\src\\txt";

    private static String DEFAULT_PATH;
    private static String DIRECTORY_PATH;

    private static Path names_path;


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

    private static int scan_file_idx(List<String> names) {
        int fn;
        String fileNum;
        while (true) {
            fileNum = sc.next();
            try {
                fn = Integer.parseInt(fileNum);
                if (0 <= fn && fn <= names.size()-1) {
                    break;
                } else {
                    System.out.println("잘못된 파일 번호입니다. 제대로 된 파일 번호를 입력해주세요.");
                    print_names(names);
                }
            } catch (NumberFormatException nfe) {
                System.out.println("입력한 값이 숫자가 아닙니다. 다시 입력해 주세요.");
            }
        }
        return fn;
    }

    private static void cmd_new_question() throws IOException {

        List<String> names;
        names = borrow_data("names");

        if (names.isEmpty()) {
            System.out.println("파일이 하나도 없습니다. 파일 추가 모드로 들어가서 파일을 추가해주세요.");
            System.out.println("메인으로 돌아갑니다.");
            return;
        }

        System.out.println("질문을 추가할 파일명을 입력해주세요. 번호로 입력해주셔야 합니다.");
        print_names(names);

        // 파일 번호를 받아오는 함수
        int fn = scan_file_idx(names);

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
    }

    private static boolean compare_names(List<String> a, List<String> b) {
        if (a.size() != b.size()) return false;
        List<String> sa = a.stream().sorted().collect(Collectors.toList());
        List<String> sb = b.stream().sorted().collect(Collectors.toList());

        for (int i=0; i<sa.size(); i++) {
            if (!sa.get(i).equals(sb.get(i))) return false;
        }
        return true;
    }

    private static void set_paths() throws IOException {
        DIRECTORY_PATH = new File(DIRECTORY_RELATIVE_PATH).getCanonicalPath();
        DEFAULT_PATH = DIRECTORY_PATH + "\\";
        names_path = Paths.get(DEFAULT_PATH + "names.txt");
    }

    private static void can_start() throws ClientFaultException, IOException {
        set_paths();
        Path possible_directory = Paths.get(DIRECTORY_PATH);
        Path possible_default_path = Paths.get(DEFAULT_PATH);
        if (!Files.isDirectory(possible_directory)) {
            throw new ClientFaultException("INVALID PATH - DIRECTORY PATH", new NotDirectoryException(possible_directory.toString()));
        }
        if (!Files.isDirectory(possible_default_path)) {
            throw new ClientFaultException("INVALID PATH - DEFAULT PATH", new NotDirectoryException(possible_default_path.toString()));
        }
        if (Files.notExists(names_path)) {
            System.out.println("names.txt 파일이 없습니다. 파일을 새로 생성합니다.");
            Files.createFile(names_path);
        }
    }

    private static void check_integrity() throws IOException {
        System.out.println("파일명 리스트 무결성 검사를 시작합니다.");
        List<String> names = borrow_data("names");

        // 실제 파일명  리스트 가져오기
        File dir = new File(DIRECTORY_PATH);
        String[] raw_exact_names = Optional.ofNullable(dir.list()).orElse(new String[0]);
        List<String> exact_names = Arrays.stream(raw_exact_names)
                .filter(s -> !s.equals("names.txt")).map(a -> a.substring(0, a.indexOf(".")))
                        .collect(Collectors.toList());

        if (compare_names(names, exact_names)) {
            System.out.println("무결성 검사가 완료되었습니다. 모든 파일이 파일명 리스트에 정상적으로 등록되어 있습니다. 메인으로 돌아갑니다.");
        } else {
            System.out.println("무결성 검사에 실패했습니다. 실제로 생성되어 있는 파일을 기준으로 파일명 리스트를 다시 작성합니다.");
            new BufferedWriter(new FileWriter(names_path.toFile(), false)).close(); // names.txt 내용 지우기
            exact_names.forEach(s -> {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(names_path.toFile(), true));
                    writer.write(s);
                    writer.newLine();
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            System.out.println("파일명 리스트를 다시 작성했습니다. 무결성 검사를 종료합니다. 메인으로 돌아갑니다.");
        }
    }

    private static int scan_limit(int max) {
        int fn = 0;
        while (true) {
            String input = sc.next();
            if (input.equals("-1")) return -1;
            try {
                fn = Integer.parseInt(input);
            } catch (NumberFormatException noe) {
                System.out.println("숫자를 입력해 주셔야 합니다.");
            }
            if (fn <= 0 || fn >= max) {
                System.out.println("1부터 " + (max-1) + "까지의 숫자만 가능합니다.");
            } else break;
        }
        return fn;
    }

    private static int scan_int_order() {
        int fn;
        while (true) {
            String order = sc.next();
            try {
                fn = Integer.parseInt(order);
                return fn;
            } catch (NumberFormatException nfe) {
                System.out.println("숫자가 아닙니다. 다시 입력해주세요.");
            }
        }
    }

    private static void test(List<String> data, List<Integer> idx_list, int limit) {
        int cnt = 0;
        System.out.println("테스트를 시작합니다. 다음 문제를 보고 싶다면 1을, 테스트를 강제 종료하려면 -1을 입력해주세요.");
        while (cnt < limit) {
            int order = scan_int_order();
            if (order == 1) {
                System.out.println(data.get(idx_list.get(cnt)));
                cnt++;
            } else if (order == -1) {
                System.out.println("테스트를 종료하고 메인으로 돌아갑니다.");
            } else {
                System.out.println("잘못된 커맨드입니다. 다음 문제를 보고 싶다면 1을, 테스트를 강제 종료하려면 -1을 입력해주세요.");
            }
        }
    }

    private static void test_mode_each() throws IOException {

        System.out.println("개별 파일 테스트 모드를 시작합니다.");
        List<String> names_list = borrow_data("names");
        if (names_list.isEmpty()) {
            System.out.println("카테고리가 하나도 없습니다. 카테고리 추가 모드로 들어가서 새로운 카테고리를 추가하세요.");
            return;
        }
        System.out.println("파일명을 선택해주세요.");
        print_names(names_list);
        int names_idx = scan_file_idx(names_list);
        List<String> data = borrow_data(names_list.get(names_idx));
        if (data.isEmpty()) {
            System.out.println("선택한 파일에 질문이 하나도 없습니다. 질문 등록 모드로 들어가서 새로운 질문을 추가하세요.");
            return;
        }
        test_mode(data);
        System.out.println("개별 파일 테스트 모드를 종료합니다.");
    }

    private static void test_mode_sum() throws IOException {
        List<String> names_list = borrow_data("names");
        if (names_list.isEmpty()) {
            System.out.println("카테고리가 하나도 없습니다. 카테고리 추가 모드로 들어가서 새로운 카테고리를 추가하세요.");
            return;
        }
        System.out.println("전체 파일 테스트 모드를 시작합니다.");

        List<String> data = new ArrayList<>();
        names_list.forEach(s -> {
            try {
                data.addAll(borrow_data(s));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        if (data.isEmpty()) {
            System.out.println("질문이 하나도 없어 테스트를 진행할 수 없습니다. 질문 등록 모드로 들어가서 새로운 질문을 추가하세요.");
            return;
        }

        test_mode(data);
        System.out.println("전체 파일 테스트 모드를 종료합니다.");
    }

    private static void test_mode(List<String> data) {
        List<Integer> idx_list = IntStream.range(0, data.size()).boxed().collect(Collectors.toList());

        System.out.println("문제를 몇 개 뽑을까요?");
        System.out.println("테스트 모드를 강제 종료하고 싶다면 -1을 입력해주세요");
        int limit = scan_limit(data.size());
        if (limit == -1) {
            System.out.println("테스트 모드를 종료하고 메인으로 돌아갑니다.");
        }

        while (true) {
            Collections.shuffle(idx_list);
            test(data, idx_list, limit);
            System.out.println("테스트를 성공적으로 종료했습니다.");
            System.out.println("같은 설정으로 테스트를 한 번 더 보려면 1을, 이대로 테스트 모드를 종료하려면 -1을 입력해주세요.");
            int order = scan_int_order();
            if (order == -1) break;
        }
    }

    public static void main(String[] args) throws IOException {

        System.out.println("##============Interview Question Random Generator!============##");

        try {
            can_start();
            check_integrity();
        } catch (ClientFaultException cfe) {
            cfe.printStackTrace();
            System.out.println("path를 수정한 후 다시 어플리케이션을 실행해 주세요.");
            exit(0);
        }

        System.out.println("다음의 모드 중 하나를 선택해주세요.");
        int fn;
        while (true) {
            System.out.println("1. 카테고리 추가 모드");
            System.out.println("2. 새로운 질문 추가 모드");
            System.out.println("3. 개별 파일 테스트 모드");
            System.out.println("4. 전체 파일 테스트 모드");
            fn = scan_int_order();
            if (fn != 1 && fn != 2 && fn != 3 && fn != 4) {
                System.out.println("잘못된 명령어입니다. 다시 선택해주세요.");
            } else break;
        }

        if (fn == 1) {
            cmd_new_category();
        } else if (fn == 2) {
            cmd_new_question();
        } else if (fn == 3) {
            test_mode_each();
        } else {
            test_mode_sum();
        }

        System.out.println("##============어플리케이션을 종료합니다.============##");
    }
}