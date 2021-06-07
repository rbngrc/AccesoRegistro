package dam2.add.p8;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Operaciones ops = new Operaciones();

		boolean salir = false;
		int opcion = 0;

		while (!salir) {
			System.out.println("Elija una opcion");
			System.out.println("1 - acceder");
			System.out.println("2 - registrarse");
			System.out.println("3 - salir");
			try {
				System.out.println("opcion");
				opcion = sc.nextInt();

				switch (opcion) {
				case 1:
					ops.usuario();
					break;
				case 2:
					ops.registroUsuario();
					break;
				case 3:
					salir = true;
					break;
				default:
					System.out.println("\nElije un numero entre 1 y 3\n");
					break;
				}

			} catch (InputMismatchException e) {
				System.out.println("\nDebe insertar un numero\n");
				sc.nextLine();
			}

		}

	}

}
