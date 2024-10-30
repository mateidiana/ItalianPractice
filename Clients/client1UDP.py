import socket

def get_guess():
    guess = input("Enter your answer: ")
    return guess + '\n'


def display_game_state(game_state):
    print("Current exercise:\n" + game_state)


def main():
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    server_address = ("127.0.0.1", 8888)

    # Notify the server the client has joined
    client_socket.sendto(b"JOIN", server_address)

    while True:
        # Receive game state (exercise prompt)
        game_state, _ = client_socket.recvfrom(1024)
        game_state = game_state.decode('utf-8')
        exercises_left, _ = client_socket.recvfrom(1024)
        exercises_left = exercises_left.decode('utf-8')
        lives_remaining, _ = client_socket.recvfrom(1024)
        lives_remaining = lives_remaining.decode('utf-8')

        if "0" in exercises_left:
            print("You won!")
            break

        if "0" in lives_remaining:
            print("You lost!")
            break

        # Check for win/lose messages
        if "won" in game_state.lower() or "lost" in game_state.lower():
            print(game_state)
            break

        print("\n")
        display_game_state(game_state)
        print("Exercises left: " + exercises_left)
        print("Lives remaining: " + lives_remaining)

        # Enter guess and send to server
        guess = get_guess()
        client_socket.sendto(guess.encode('utf-8'), server_address)

        # Receive feedback
        feedback, _ = client_socket.recvfrom(1024)
        feedback = feedback.decode('utf-8')
        print(feedback)

        # Check if game over
        if "won" in feedback.lower() or "lost" in feedback.lower():
            print(feedback)
            break

    client_socket.close()


if __name__ == "__main__":
    main()
