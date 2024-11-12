package ru.safiullina;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static BlockingQueue<String> analyzeA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> analyzeB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> analyzeC = new ArrayBlockingQueue<>(100);
    static int amountOfTexts = 10_000;

    public static void main(String[] args) throws InterruptedException {
        int lengthOfText = 100_000;

        // Создаем список для хранения создаваемых потоков для анализа текстов
        List<Thread> threads = new ArrayList<>();

        // Поток для заполнения
        Thread putThread = new Thread(() -> {
            for (int i = 0; i < amountOfTexts; i++) {
                // Генерируем строку
                String text = generateText("abc", lengthOfText);
                //System.out.println(text + ", a = " + countLetters("a", text) +
                //        ", b = " + countLetters("b", text) + ", c = " + countLetters("c", text) );

                // Записываем строку в три потока
                try {
                    analyzeA.put(text);
                    analyzeB.put(text);
                    analyzeC.put(text);
                } catch (InterruptedException e) {
                    return;
                }

            }
        });

        // Добавим поток в список
        threads.add(putThread);
        // Запустим поток генерации текстов
        putThread.start();

        // Потоки для анализа текстов
        Thread A = new Thread(() -> logic("a", analyzeA));
        Thread B = new Thread(() -> logic("b", analyzeB));
        Thread C = new Thread(() -> logic("c", analyzeC));

        // Добавим потоки в список
        threads.add(A);
        threads.add(B);
        threads.add(C);

        // Стартуем потоки
        A.start();
        B.start();
        C.start();

        // ждем пока все потоки из списка завершатся
        for (Thread thread : threads) {
            thread.join();
        }

    }

    /**
     * Логика анализа текстов в очереди текстов
     *
     * @param letter --- буква, которую анализирует поток
     * @param queue  -- очередь текстов
     */
    public static void logic(String letter, BlockingQueue<String> queue) {
        int max = 0;
        String maxText = "";

        for (int i = 0; i < amountOfTexts; i++) {
            try {
                // Берем по одному тексту из очереди
                String text = queue.take();
                //System.out.println(letter + " " + text);

                // Считаем количество раз, которое встречается заданная буква
                int amount = countLetters(letter, text);
                // Если больше чем в предыдущем тексте, сохраним количество и текст
                if (amount > max) {
                    max = amount;
                    maxText = text;
                }
            } catch (InterruptedException e) {
                return;
            }
        }

        System.out.printf("\nПример строки с самым большим количеством символов %s, количество = %d, сама строка: \n %s \n ",
                letter, max, maxText);

    }

    /**
     * Подсчитываем количество раз, которое заданная буква встретилась
     *
     * @param letter --- заданная буква
     * @param text   -- текст, где ищем букву
     * @return -- возвращаем количество
     */
    public static int countLetters(String letter, String text) {
        return text.length() - text.replace(String.valueOf(letter), "").length();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}