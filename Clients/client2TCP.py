import socket


def get_guess():
    guess = input("Enter your answer: ")
    return guess + '\n'


def display_game_state(game_state):
    print(f"Current exercise: {game_state}")


def main():
    # create client
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_socket.connect(("127.0.0.1", 8888))

    # get the welcome message from the server
    welcome_message = client_socket.recv(1024).decode('utf-8')
    print(welcome_message)

    while True:

        # get the information from the sever
        game_state = client_socket.recv(1024).decode('utf-8')
        exercises_left = client_socket.recv(1024).decode('utf-8')
        lives_remaining = client_socket.recv(1024).decode('utf-8')

        # check if game ended
        if "0" in exercises_left:
            print("You won!")
            break

        if "0" in lives_remaining:
            print("You lost!")
            break

        # check if game ended
        if "won" in game_state.lower():
            print("You won!")
            break

        if "lost" in game_state.lower():
            print("You lost!")
            break

        if game_state == "":
            print("You lost!")
            break

        # Display lives remaining
        display_game_state(game_state)
        print(exercises_left)
        print(lives_remaining)

        # send guess to server
        guess = get_guess()
        client_socket.send(guess.encode('utf-8'))

        # get feedback
        feedback = client_socket.recv(1024).decode('utf-8')

        if "lost" in feedback.lower():
            print("You lost!")
            break

        print(feedback)

    client_socket.close()


if __name__ == "__main__":
    main()