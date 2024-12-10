import java.util.Arrays;

public class Solution {

    // Размер блока в байтах
    private static final int BLOCK_SIZE = 12;
    // Число раундов
    private static final int ROUNDS = 16;
    // Итерационные ключи
    private static final byte[][] KEYS = {
            {0x6f, 0x5c, (byte) 0xda, (byte) 0x8c, (byte) 0xdd, 0x5a},
            {(byte) 0xf1, 0x0a, (byte) 0xb4, (byte) 0xe8, 0x62, (byte) 0xd7},
            {0x34, 0x10, (byte) 0xf5, (byte) 0xa3, (byte) 0xa5, 0x7a},
            {0x78, (byte) 0xb8, (byte) 0xaa, (byte) 0xd8, 0x24, 0x7e},
            {0x7b, 0x1e, 0x4b, 0x67, 0x73, 0x05},
            {(byte) 0xe6, (byte) 0xe0, (byte) 0xd9, (byte) 0xae, (byte) 0xa0, (byte) 0xbd},
            {0x78, 0x3d, (byte) 0xca, 0x5e, (byte) 0xcb, 0x2f},
            {(byte) 0xb1, (byte) 0x81, (byte) 0x90, 0x40, 0x00, (byte) 0xef},
            {0x25, (byte) 0xdc, 0x38, (byte) 0xec, (byte) 0xb4, (byte) 0xb0},
            {0x01, (byte) 0x97, 0x38, 0x5c, 0x10, (byte) 0xbe},
            {0x0e, (byte) 0x8a, (byte) 0x97, (byte) 0xae, 0x02, (byte) 0x82},
            {(byte) 0xfd, (byte) 0x85, 0x70, 0x38, 0x2b, 0x68},
            {(byte) 0xaf, 0x19, (byte) 0xed, 0x7b, 0x26, (byte) 0xa3},
            {(byte) 0xed, 0x3b, 0x1e, 0x5c, 0x41, (byte) 0x96},
            {(byte) 0xcd, (byte) 0xfa, 0x2b, 0x28, (byte) 0xad, 0x08},
            {(byte) 0x84, 0x1b, 0x2c, 0x0c, 0x50, 0x41}
    };

    // Перестановка для функции F
    private static final int[] PERMUTATION = {
            47, 46, 28, 43, 37, 41, 39, 7, 32, 12, 2, 35, 30, 11, 14, 4, 18, 17, 0, 23, 29, 25, 3, 26, 33, 40, 15, 8, 13, 19, 22, 20, 31, 16, 21, 27, 9, 10, 36, 34, 6, 24, 38, 5, 1, 42, 44, 45
    };

    // Функция F: перестановка 48 бит
    private static byte[] F(byte[] input) {
        byte[] output = new byte[6];
        for (int i = 0; i < 48; i++) {
            int bitPosition = PERMUTATION[i];
            int byteIndex = bitPosition / 8;
            int bitIndex = bitPosition % 8;
            boolean bit = (input[byteIndex] & (1 << (7 - bitIndex))) != 0;
            int outputByteIndex = i / 8;
            int outputBitIndex = i % 8;
            if (bit) {
                output[outputByteIndex] |= (1 << (7 - outputBitIndex));
            }
        }
        return output;
    }

    // Сложение по модулю 2 (XOR)
    private static byte[] xor(byte[] a, byte[] b) {
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }

    // Шифрование блока с выводом раундов
    public static byte[] encryptBlock(byte[] block) {
        byte[] L = Arrays.copyOfRange(block, 0, 6);
        byte[] R = Arrays.copyOfRange(block, 6, 12);

        System.out.println("Инициализация:");
        System.out.println("L: " + Arrays.toString(L));
        System.out.println("R: " + Arrays.toString(R));

        for (int round = 0; round < ROUNDS; round++) {
            System.out.println("\nРаунд " + round);
            System.out.println("Начальное состояние регистров:");
            System.out.println("L: " + Arrays.toString(L));
            System.out.println("R: " + Arrays.toString(R));

            byte[] T = xor(R, KEYS[round]);
            System.out.println("После сложения с итерационным ключом K" + round + ":");
            System.out.println("T: " + Arrays.toString(T));

            byte[] V = F(T);
            System.out.println("F(T): " + Arrays.toString(V));

            byte[] newL = R;
            byte[] newR = xor(L, V);
            System.out.println("Состояние регистров после сложения с F(L" + round + "):");
            System.out.println("L: " + Arrays.toString(newL));
            System.out.println("R: " + Arrays.toString(newR));

            L = newL;
            R = newR;
        }

        byte[] encryptedBlock = new byte[BLOCK_SIZE];
        System.arraycopy(L, 0, encryptedBlock, 0, 6);
        System.arraycopy(R, 0, encryptedBlock, 6, 6);
        return encryptedBlock;
    }

    // Расшифрование блока с выводом раундов
    public static byte[] decryptBlock(byte[] block) {
        byte[] L = Arrays.copyOfRange(block, 0, 6);
        byte[] R = Arrays.copyOfRange(block, 6, 12);

        System.out.println("Инициализация:");
        System.out.println("L: " + Arrays.toString(L));
        System.out.println("R: " + Arrays.toString(R));

        for (int round = ROUNDS - 1; round >= 0; round--) {
            System.out.println("\nРаунд " + round);
            System.out.println("Начальное состояние регистров:");
            System.out.println("L: " + Arrays.toString(L));
            System.out.println("R: " + Arrays.toString(R));

            byte[] T = xor(L, KEYS[round]);
            System.out.println("После сложения с итерационным ключом K" + round + ":");
            System.out.println("T: " + Arrays.toString(T));

            byte[] V = F(T);
            System.out.println("F(T): " + Arrays.toString(V));

            byte[] newR = L;
            byte[] newL = xor(R, V);
            System.out.println("Состояние регистров после сложения с F(L" + round + "):");
            System.out.println("L: " + Arrays.toString(newL));
            System.out.println("R: " + Arrays.toString(newR));

            L = newL;
            R = newR;
        }

        byte[] decryptedBlock = new byte[BLOCK_SIZE];
        System.arraycopy(L, 0, decryptedBlock, 0, 6);
        System.arraycopy(R, 0, decryptedBlock, 6, 6);
        return decryptedBlock;
    }

    // Пример использования
    public static void main(String[] args) {
        byte[] plaintext = {
                0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b,
                0x3c, 0x3d, 0x3e, 0x3f, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47,
                0x48, 0x49, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x4f, 0x30, 0x31, 0x32, 0x33
        };

        System.out.println("Исходный текст: " + Arrays.toString(plaintext));

        // Шифрование
        byte[] encrypted = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i += BLOCK_SIZE) {
            byte[] block = Arrays.copyOfRange(plaintext, i, i + BLOCK_SIZE);
            byte[] encryptedBlock = encryptBlock(block);
            System.arraycopy(encryptedBlock, 0, encrypted, i, BLOCK_SIZE);
        }
        System.out.println("Зашифрованный текст: " + Arrays.toString(encrypted));

        // Расшифрование
        byte[] decrypted = new byte[encrypted.length];
        for (int i = 0; i < encrypted.length; i += BLOCK_SIZE) {
            byte[] block = Arrays.copyOfRange(encrypted, i, i + BLOCK_SIZE);
            byte[] decryptedBlock = decryptBlock(block);
            System.arraycopy(decryptedBlock, 0, decrypted, i, BLOCK_SIZE);
        }
        System.out.println("Расшифрованный текст: " + Arrays.toString(decrypted));
    }
}