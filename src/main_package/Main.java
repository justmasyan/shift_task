package main_package;

import java.io.*;
import java.util.Arrays;

public class Main {
    static boolean TYPE_SORT;
    static boolean TYPE_ELEMENTS;
    static String FILENAME_OUT;

    public static void main(String[] args) throws IOException {
        int index = 0;

        switch (args[index]) {
            case "-a":
                TYPE_SORT = true;
                System.out.println("Cортировка слиянием вверх");
                index++;
                break;
            case "-d":
                TYPE_SORT = false;
                System.out.println("Cортировка слиянием вниз");
                index++;
                break;
            default:
                System.out.println("По стандарту сортировка слиянием вверх");
                TYPE_SORT = true;
                break;
        }

        switch (args[index++]) {
            case "-i":
                TYPE_ELEMENTS = true;
                System.out.println("По стандарту формат элементов типа Int");
                break;
            case "-s":
                TYPE_ELEMENTS = false;
                System.out.println("По стандарту формат элементов типа String");
                break;
            default:
                System.out.println("Неккоректный тип. По стандарту формат элементов типа String");
                TYPE_ELEMENTS = false;
                break;
        }

        FILENAME_OUT = args[index++];

        //Предварительная проверка на то, что файлы предварительно отсортированы

        for (int i = index; i < args.length; i++) {
            System.out.println("Открыт " + (i - index + 1) + " файл");
            try {
                BufferedReader breader = new BufferedReader(new FileReader(args[i]));
                String next, previous = null;

                while (breader.ready()) {
                    next = breader.readLine();
                    if (TYPE_ELEMENTS) {
                        try {
                            int num_next = Integer.parseInt(next);
                            if (previous != null && (Integer.parseInt(previous) < num_next ^ TYPE_SORT)) {
                                breader.close();
                                sort_file(args[i]);
                                break;
                            }
                            previous = next;
                        } catch (NumberFormatException e) {
                            System.out.println("error number`s format");
                        }
                    } else {
                        if (!next.contains(" ")) {
                            if (previous != null && (previous.compareTo(next) < 0 ^ TYPE_SORT)) {
                                breader.close();
                                sort_file(args[i]);
                                break;
                            }
                            previous = next;
                        }


                    }

                }
                breader.close();
            } catch (FileNotFoundException e) {
                System.out.println("Файл " + args[i] + " не получилось открыть.");
            } catch (IOException e) {
                System.out.println("Ошибка чтения файла: " + args[i]);
            }
        }

        String[] arrays_files = Arrays.copyOfRange(args,index,args.length);

        unite_file(arrays_files,FILENAME_OUT);

        System.out.println();

    }

    //Сортировка файлов

    private static void sort_file(String file_name) throws IOException{
        File infile = new File(file_name);
        BufferedReader breader = new BufferedReader(new FileReader(infile));
        int num_of_files = 1;
        String next,previous = null;
        File fileout = new File(num_of_files++ + ".txt");
        fileout.createNewFile();
        FileWriter out = new FileWriter(fileout);

        while (breader.ready()) {
            next = breader.readLine();
            if (TYPE_ELEMENTS) {
                try {
                    int num_next = Integer.parseInt(next);
                    if (previous != null && (Integer.parseInt(previous) < num_next ^ TYPE_SORT)) {
                        out.close();
                        fileout = new File(num_of_files++ + ".txt");
                        fileout.createNewFile();
                        out = new FileWriter(fileout);
                    }
                    out.write(next + "\n");
                    previous = next;
                } catch (NumberFormatException e) {

                }
            } else {
                if (!next.contains(" ")) {
                    if (previous != null && (previous.compareTo(next) < 0 ^ TYPE_SORT)) {
                        out.close();
                        fileout = new File(num_of_files++ + ".txt");
                        fileout.createNewFile();
                        out = new FileWriter(fileout);
                    }
                    out.write(next + "\n");
                    previous = next;
                }
            }

        }

        breader.close();
        out.close();
        infile.delete();

        String[] array_tmp_files = new String[num_of_files-1];

        for(int i = 0;i < array_tmp_files.length;i++){
            array_tmp_files[i] = i+1 + ".txt";
        }

        unite_file(array_tmp_files,file_name);

        for(String str:array_tmp_files){
            fileout = new File(str);
            fileout.delete();
        }

    }

    //Объединение нескольких отсортированных файлов в один путем слияния

    private static void unite_file(String[] file_names,String name_out_file) throws IOException {
        File outlol = new File(name_out_file);
        outlol.createNewFile();
        FileWriter out = new FileWriter(outlol);
        String specific_value = null;
        int index_specific_value = -1;
        String[] values_streams = new String[file_names.length];
        BufferedReader[] array_streams = new BufferedReader[file_names.length];
        for (int i = 0; i < file_names.length; i++) {
            try {
                array_streams[i] = new BufferedReader(new FileReader(file_names[i]));
            } catch (FileNotFoundException e){

            }
        }

        for (int i = 0; i < file_names.length; i++) {
            while (array_streams[i] != null && array_streams[i].ready()) {
                values_streams[i] = array_streams[i].readLine();
                if (TYPE_ELEMENTS) {
                    try {
                        int number = Integer.parseInt(values_streams[i]);
                        if (specific_value == null || (Integer.parseInt(specific_value) < number ^ TYPE_SORT)) {
                            specific_value = values_streams[i];
                            index_specific_value = i;

                        }
                        break;
                    } catch (NumberFormatException e) {
                        values_streams[i] = null;

                    }

                } else {
                    if (!values_streams[i].contains(" ")) {
                        if (specific_value == null || (specific_value.compareTo(values_streams[i]) < 0 ^ TYPE_SORT)) {
                            specific_value = values_streams[i];
                            index_specific_value = i;
                        }
                        break;
                    } else {
                        values_streams[i] = null;
                    }
                }
            }
        }
        while (specific_value != null) {
            out.write(specific_value + "\n");

            while (true) {
                if (array_streams[index_specific_value].ready()) {
                    values_streams[index_specific_value] = array_streams[index_specific_value].readLine();
                    if (TYPE_ELEMENTS) {
                        try {
                            Integer.parseInt(values_streams[index_specific_value]);
                            break;
                        } catch (NumberFormatException e) {
                            values_streams[index_specific_value] = null;
                        }
                    } else {
                        if (values_streams[index_specific_value].contains(" ")) {
                            values_streams[index_specific_value] = null;
                        } else {
                            break;
                        }
                    }
                } else {
                    values_streams[index_specific_value] = null;
                    break;
                }
            }
            specific_value = null;
            index_specific_value = -1;

            for (int i = 0; i < values_streams.length; i++) {
                if (values_streams[i] == null)
                    continue;

                if (TYPE_ELEMENTS) {
                    if (specific_value == null || (Integer.parseInt(specific_value) < Integer.parseInt(values_streams[i]) ^ TYPE_SORT)) {
                        specific_value = values_streams[i];
                        index_specific_value = i;

                    }
                } else {
                    if (specific_value == null || (specific_value.compareTo(values_streams[i]) < 0 ^ TYPE_SORT)) {
                        specific_value = values_streams[i];
                        index_specific_value = i;
                    }
                }
            }
        }


        out.close();
        for (int i = 0; i < file_names.length; i++) {
            if(array_streams[i] != null)
            array_streams[i].close();
        }

    }
}

