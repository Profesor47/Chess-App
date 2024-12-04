package com.example.chessgame.Game;


public class MoveValidator {
    private static final int BOARD_SIZE = 8;

    public static boolean isValidMove(ChessPiece piece, int targetRow, int targetCol, ChessPiece[][] board) {
        if (targetRow < 0 || targetRow >= BOARD_SIZE || targetCol < 0 || targetCol >= BOARD_SIZE) {
            return false;
        }

        // Can't capture your own piece
        if (board[targetRow][targetCol] != null &&
                board[targetRow][targetCol].isWhite() == piece.isWhite()) {
            return false;
        }

        switch (piece.getType()) {
            case PAWN:
                return isValidPawnMove(piece, targetRow, targetCol, board);
            case ROOK:
                return isValidRookMove(piece, targetRow, targetCol, board);
            case KNIGHT:
                return isValidKnightMove(piece, targetRow, targetCol, board);
            case BISHOP:
                return isValidBishopMove(piece, targetRow, targetCol, board);
            case QUEEN:
                return isValidQueenMove(piece, targetRow, targetCol, board);
            case KING:
                return isValidKingMove(piece, targetRow, targetCol, board);
            default:
                return false;
        }
    }

    private static boolean isValidPawnMove(ChessPiece piece, int targetRow, int targetCol, ChessPiece[][] board) {
        int currentRow = piece.getRow();
        int currentCol = piece.getCol();
        int direction = piece.isWhite() ? -1 : 1;

        // Basic one square move
        if (currentCol == targetCol && targetRow == currentRow + direction) {
            return board[targetRow][targetCol] == null;
        }

        // Initial two square move
        if (currentCol == targetCol &&
                ((piece.isWhite() && currentRow == 6 && targetRow == 4) ||
                        (!piece.isWhite() && currentRow == 1 && targetRow == 3))) {
            return board[targetRow][targetCol] == null &&
                    board[currentRow + direction][currentCol] == null;
        }

        // Regular capture move
        if (Math.abs(targetCol - currentCol) == 1 && targetRow == currentRow + direction) {
            // Check for normal capture
            if (board[targetRow][targetCol] != null &&
                    board[targetRow][targetCol].isWhite() != piece.isWhite()) {
                return true;
            }

            // Check for en passant
            if (board[targetRow][targetCol] == null && // Target square is empty
                    board[currentRow][targetCol] != null && // Adjacent square has a piece
                    board[currentRow][targetCol].getType() == PieceType.PAWN && // Adjacent piece is a pawn
                    board[currentRow][targetCol].isWhite() != piece.isWhite() && // Adjacent pawn is opponent's
                    board[currentRow][targetCol].isJustMadeDoubleMove()) { // Adjacent pawn just made double move
                return true;
            }
        }

        return false;
    }

    private static boolean isValidRookMove(ChessPiece piece, int targetRow, int targetCol, ChessPiece[][] board) {
        int currentRow = piece.getRow();
        int currentCol = piece.getCol();

        if (currentRow != targetRow && currentCol != targetCol) {
            return false;
        }

        // Check path is clear
        if (currentRow == targetRow) {
            int step = Integer.compare(targetCol, currentCol);
            for (int col = currentCol + step; col != targetCol; col += step) {
                if (board[currentRow][col] != null) {
                    return false;
                }
            }
        } else {
            int step = Integer.compare(targetRow, currentRow);
            for (int row = currentRow + step; row != targetRow; row += step) {
                if (board[row][currentCol] != null) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isValidKnightMove(ChessPiece piece, int targetRow, int targetCol, ChessPiece[][] board) {
        int rowDiff = Math.abs(targetRow - piece.getRow());
        int colDiff = Math.abs(targetCol - piece.getCol());

        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }

    private static boolean isValidBishopMove(ChessPiece piece, int targetRow, int targetCol, ChessPiece[][] board) {
        int rowDiff = Math.abs(targetRow - piece.getRow());
        int colDiff = Math.abs(targetCol - piece.getCol());

        if (rowDiff != colDiff) {
            return false;
        }

        int rowStep = Integer.compare(targetRow, piece.getRow());
        int colStep = Integer.compare(targetCol, piece.getCol());

        int currentRow = piece.getRow() + rowStep;
        int currentCol = piece.getCol() + colStep;

        while (currentRow != targetRow) {
            if (board[currentRow][currentCol] != null) {
                return false;
            }
            currentRow += rowStep;
            currentCol += colStep;
        }

        return true;
    }

    private static boolean isValidQueenMove(ChessPiece piece, int targetRow, int targetCol, ChessPiece[][] board) {
        return isValidRookMove(piece, targetRow, targetCol, board) ||
                isValidBishopMove(piece, targetRow, targetCol, board);
    }

    private static boolean isValidKingMove(ChessPiece piece, int targetRow, int targetCol, ChessPiece[][] board) {
        int rowDiff = Math.abs(targetRow - piece.getRow());
        int colDiff = Math.abs(targetCol - piece.getCol());

        // Regular king move
        if (rowDiff <= 1 && colDiff <= 1) {
            return true;
        }

        // Castling move
        if (rowDiff == 0 && colDiff == 2) {
            // Check if it's the king's initial position
            if ((piece.isWhite() && piece.getRow() != 7) || (!piece.isWhite() && piece.getRow() != 0)) {
                return false;
            }
            if (piece.getRow() != targetRow || piece.getCol() != 4) {  // King must be on e1/e8
                return false;
            }

            // Determine if kingside or queenside castling
            boolean isKingside = targetCol == 6;
            int rookCol = isKingside ? 7 : 0;

            // Check if the rook is in place
            ChessPiece rook = board[targetRow][rookCol];
            if (rook == null || rook.getType() != PieceType.ROOK || rook.isWhite() != piece.isWhite()) {
                return false;
            }

            // Check if path is clear
            if (isKingside) {
                if (board[targetRow][5] != null || board[targetRow][6] != null) {
                    return false;
                }
            } else {
                if (board[targetRow][1] != null || board[targetRow][2] != null || board[targetRow][3] != null) {
                    return false;
                }
            }

            // Check if king is currently in check
            if (isSquareUnderAttack(piece.getRow(), piece.getCol(), board, !piece.isWhite())) {
                return false;
            }

            // Check if squares the king passes through are under attack
            if (isKingside) {
                // Check f1/f8 and g1/g8
                if (isSquareUnderAttack(targetRow, 5, board, !piece.isWhite()) ||
                        isSquareUnderAttack(targetRow, 6, board, !piece.isWhite())) {
                    return false;
                }
            } else {
                // Check d1/d8 and c1/c8
                if (isSquareUnderAttack(targetRow, 3, board, !piece.isWhite()) ||
                        isSquareUnderAttack(targetRow, 2, board, !piece.isWhite())) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
    private static boolean isSquareUnderAttack(int row, int col, ChessPiece[][] board, boolean byWhite) {
        // Check attacks from all opponent pieces
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                ChessPiece attacker = board[r][c];
                if (attacker != null && attacker.isWhite() == byWhite) {
                    // For pawns, we need to check diagonal captures specifically
                    if (attacker.getType() == PieceType.PAWN) {
                        int direction = attacker.isWhite() ? -1 : 1;
                        if (attacker.getCol() != col &&
                                row == attacker.getRow() + direction &&
                                Math.abs(col - attacker.getCol()) == 1) {
                            return true;
                        }
                    }
                    // For other pieces, we can use the normal move validation
                    else if (isValidMove(attacker, row, col, board)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    
}