package com.example.chessgame.Game;

import android.content.Context;

import android.widget.GridLayout;
import android.graphics.Color;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;


public class ChessBoard {
    private Context context;
    private GridLayout gridLayout;
    private ImageView[][] cells;
    private ChessPiece[][] pieces;
    private static final int BOARD_SIZE = 8;
    private static final int CELL_SIZE = 48;
    private GameState gameState;
    private static final int HIGHLIGHT_COLOR = Color.rgb(255, 255, 0);
    private GameStatusManager gameStatusManager;
    private static final int POSSIBLE_MOVE_COLOR = Color.rgb(119, 187, 116);  // Light green
    private static final int POSSIBLE_CAPTURE_COLOR = Color.rgb(187, 119, 116);

    public ChessBoard(Context context, GridLayout gridLayout, GameState gameState, GameStatusManager gameStatusManager) {
        this.context = context;
        this.gridLayout = gridLayout;
        this.cells = new ImageView[BOARD_SIZE][BOARD_SIZE];
        this.pieces = new ChessPiece[BOARD_SIZE][BOARD_SIZE];
        this.gameState = gameState;
        this.gameStatusManager = gameStatusManager;
        initializeBoard();
        setupInitialPieces();
        setupCellClickListeners();
    }

    private void initializeBoard() {
        int dpSize = (int) (CELL_SIZE * context.getResources().getDisplayMetrics().density);

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ImageView cell = new ImageView(context);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = dpSize;
                params.height = dpSize;
                cell.setLayoutParams(params);

                if ((row + col) % 2 == 0) {
                    cell.setBackgroundColor(Color.rgb(238, 238, 210)); // Light squares
                } else {
                    cell.setBackgroundColor(Color.rgb(118, 150, 86)); // Dark squares
                }

                cell.setScaleType(ImageView.ScaleType.FIT_CENTER);
                cell.setPadding(dpSize / 8, dpSize / 8, dpSize / 8, dpSize / 8);

                cells[row][col] = cell;
                gridLayout.addView(cell);
            }
        }
    }


    private void setupInitialPieces() {
        // Setup black pieces
        setupPieceRow(0, false);
        setupPawnRow(1, false);

        // Setup white pieces
        setupPieceRow(7, true);
        setupPawnRow(6, true);

        updateBoardDisplay();
    }

    private void setupPieceRow(int row, boolean isWhite) {
        pieces[row][0] = new ChessPiece(PieceType.ROOK, isWhite, row, 0);
        pieces[row][1] = new ChessPiece(PieceType.KNIGHT, isWhite, row, 1);
        pieces[row][2] = new ChessPiece(PieceType.BISHOP, isWhite, row, 2);
        pieces[row][3] = new ChessPiece(PieceType.QUEEN, isWhite, row, 3);
        pieces[row][4] = new ChessPiece(PieceType.KING, isWhite, row, 4);
        pieces[row][5] = new ChessPiece(PieceType.BISHOP, isWhite, row, 5);
        pieces[row][6] = new ChessPiece(PieceType.KNIGHT, isWhite, row, 6);
        pieces[row][7] = new ChessPiece(PieceType.ROOK, isWhite, row, 7);
    }

    private void setupPawnRow(int row, boolean isWhite) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            pieces[row][col] = new ChessPiece(PieceType.PAWN, isWhite, row, col);
        }
    }

    private void setupCellClickListeners() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                final int finalRow = row;
                final int finalCol = col;
                cells[row][col].setOnClickListener(v -> handleCellClick(finalRow, finalCol));
            }
        }
    }

    private void handleCellClick(int row, int col) {
        if (gameState.isGameOver()) {
            return;
        }

        ChessPiece clickedPiece = pieces[row][col];
        ChessPiece selectedPiece = gameState.getSelectedPiece();

        if (selectedPiece == null) {
            if (clickedPiece != null && clickedPiece.isWhite() == gameState.isWhiteTurn()) {
                selectPiece(clickedPiece);
                showPossibleMoves(clickedPiece);
            }
            return;
        }

        // If clicking on a possible move
        if (isLegalMove(selectedPiece, row, col)) {
            movePiece(selectedPiece, row, col);  // Move the piece
            gameState.toggleTurn();  // Update the turn in the game state
            gameStatusManager.toggleTurn();  // Switch the timers
            gameStatusManager.updateDisplay();  // Update UI (e.g., turn text)
            checkGameStatus();  // Check for checkmate or stalemate
        }

        clearSelection();
        updateBoardDisplay();
    }


    // New method to check if a move is legal (considering check)
    private boolean isLegalMove(ChessPiece piece, int targetRow, int targetCol) {
        // First check if it's a valid move according to piece rules
        if (!MoveValidator.isValidMove(piece, targetRow, targetCol, pieces)) {
            return false;
        }

        // Then check if this move would leave or put own king in check
        return !wouldMoveResultInCheck(piece, targetRow, targetCol);
    }

    private void showPossibleMoves(ChessPiece piece) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (isLegalMove(piece, row, col)) {
                    // If there's a piece that can be captured, highlight in red
                    if (pieces[row][col] != null) {
                        cells[row][col].setBackgroundColor(POSSIBLE_CAPTURE_COLOR);
                    } else {
                        cells[row][col].setBackgroundColor(POSSIBLE_MOVE_COLOR);
                    }
                }
            }
        }
    }

    private boolean wouldMoveResultInCheck(ChessPiece piece, int targetRow, int targetCol) {
        // Store current position and piece at target
        int originalRow = piece.getRow();
        int originalCol = piece.getCol();
        ChessPiece capturedPiece = pieces[targetRow][targetCol];

        // Make temporary move
        pieces[targetRow][targetCol] = piece;
        pieces[originalRow][originalCol] = null;
        piece.setPosition(targetRow, targetCol);

        // Check if own king is in check
        boolean resultsInCheck = isKingInCheck(piece.isWhite());

        // Undo move
        pieces[originalRow][originalCol] = piece;
        pieces[targetRow][targetCol] = capturedPiece;
        piece.setPosition(originalRow, originalCol);

        return resultsInCheck;
    }

    // Updated checkmate check to use isLegalMove
    private boolean isCheckmate(boolean isWhiteKing) {
        // If not in check, can't be checkmate
        if (!isKingInCheck(isWhiteKing)) {
            return false;
        }

        // Try all possible moves for all pieces
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessPiece piece = pieces[row][col];
                if (piece != null && piece.isWhite() == isWhiteKing) {
                    // Check all possible squares
                    for (int newRow = 0; newRow < BOARD_SIZE; newRow++) {
                        for (int newCol = 0; newCol < BOARD_SIZE; newCol++) {
                            // If any legal move exists, it's not checkmate
                            if (isLegalMove(piece, newRow, newCol)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    // Updated stalemate check to use isLegalMove
    private boolean isStalemate(boolean isWhiteTurn) {
        if (isKingInCheck(isWhiteTurn)) {
            return false;
        }

        // Check if any legal moves exist
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessPiece piece = pieces[row][col];
                if (piece != null && piece.isWhite() == isWhiteTurn) {
                    for (int newRow = 0; newRow < BOARD_SIZE; newRow++) {
                        for (int newCol = 0; newCol < BOARD_SIZE; newCol++) {
                            if (isLegalMove(piece, newRow, newCol)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private void clearHighlights() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 == 0) {
                    cells[row][col].setBackgroundColor(Color.rgb(238, 238, 210)); // Light squares
                } else {
                    cells[row][col].setBackgroundColor(Color.rgb(118, 150, 86)); // Dark squares
                }
            }
        }
    }

    private void selectPiece(ChessPiece piece) {
        gameState.setSelectedPiece(piece);
        cells[piece.getRow()][piece.getCol()].setBackgroundColor(HIGHLIGHT_COLOR);
    }


    private void clearAllPawnDoubleStepFlags() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (pieces[row][col] != null) {
                    pieces[row][col].setJustMadeDoubleMove(false);
                }
            }
        }
    }

    private void movePiece(ChessPiece piece, int targetRow, int targetCol) {
        int originalRow = piece.getRow();
        int originalCol = piece.getCol();

        // Clear all previous double move flags
        clearAllPawnDoubleStepFlags();

        // Check for pawn double move
        if (piece.getType() == PieceType.PAWN && Math.abs(targetRow - originalRow) == 2) {
            piece.setJustMadeDoubleMove(true);
        }

        // Check for en passant capture
        if (piece.getType() == PieceType.PAWN &&
                targetCol != originalCol &&
                pieces[targetRow][targetCol] == null) {
            // Remove the captured pawn
            pieces[originalRow][targetCol] = null;
        }

        // Handle castling for king (existing code)
        if (piece.getType() == PieceType.KING && Math.abs(targetCol - originalCol) == 2) {
            // Kingside castling
            if (targetCol == 6) {
                ChessPiece rook = pieces[targetRow][7];
                pieces[targetRow][5] = rook;
                pieces[targetRow][7] = null;
                rook.setPosition(targetRow, 5);
            }
            // Queenside castling
            else if (targetCol == 2) {
                ChessPiece rook = pieces[targetRow][0];
                pieces[targetRow][3] = rook;
                pieces[targetRow][0] = null;
                rook.setPosition(targetRow, 3);
            }
        }

        // Move the piece (existing code)
        pieces[piece.getRow()][piece.getCol()] = null;
        pieces[targetRow][targetCol] = piece;
        piece.setPosition(targetRow, targetCol);

        // Check for pawn promotion (existing code)
        if (piece.getType() == PieceType.PAWN) {
            handlePawnPromotion(piece, targetRow);
        }
    }

    private void clearSelection() {
        gameState.setSelectedPiece(null);
        clearHighlights();
    }

    private void highlightCell(int row, int col) {
        clearHighlights();
        cells[row][col].setBackgroundColor(HIGHLIGHT_COLOR);
    }


    public void updateBoardDisplay() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessPiece piece = pieces[row][col];
                ImageView cell = cells[row][col];

                if (piece != null) {
                    cell.setImageResource(piece.getImageResource());
                } else {
                    cell.setImageResource(0);
                }
            }
        }
    }

    public ImageView getCellAt(int row, int col) {
        return cells[row][col];
    }

    public ChessPiece getPieceAt(int row, int col) {
        return pieces[row][col];
    }
    private void checkGameStatus() {
        if (isKingInCheck(gameState.isWhiteTurn())) {
            if (isCheckmate(gameState.isWhiteTurn())) {
                gameStatusManager.announceCheckmate(!gameState.isWhiteTurn());
            } else {
                gameStatusManager.announceCheck();
            }
        } else if (isStalemate(gameState.isWhiteTurn())) {
            gameStatusManager.announceStalemate();
        }
    }

    private boolean isKingInCheck(boolean isWhiteKing) {
        // Find the king
        int kingRow = -1, kingCol = -1;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessPiece piece = pieces[row][col];
                if (piece != null && piece.getType() == PieceType.KING && piece.isWhite() == isWhiteKing) {
                    kingRow = row;
                    kingCol = col;
                    break;
                }
            }
            if (kingRow != -1) break;
        }

        // Check if any opponent piece can capture the king
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessPiece piece = pieces[row][col];
                if (piece != null && piece.isWhite() != isWhiteKing) {
                    if (MoveValidator.isValidMove(piece, kingRow, kingCol, pieces)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private void handlePawnPromotion(ChessPiece pawn, int targetRow) {
        if (pawn.getType() == PieceType.PAWN && (targetRow == 0 || targetRow == 7)) {
            showPromotionDialog(pawn, targetRow);
        }
    }

    private void showPromotionDialog(ChessPiece pawn, int targetRow) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose promotion piece");

        final String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        builder.setItems(options, (dialog, which) -> {
            PieceType newType;
            switch (which) {
                case 0:
                    newType = PieceType.QUEEN;
                    break;
                case 1:
                    newType = PieceType.ROOK;
                    break;
                case 2:
                    newType = PieceType.BISHOP;
                    break;
                case 3:
                    newType = PieceType.KNIGHT;
                    break;
                default:
                    newType = PieceType.QUEEN;
            }

            // Create new piece
            ChessPiece promotedPiece = new ChessPiece(newType, pawn.isWhite(), targetRow, pawn.getCol());
            pieces[targetRow][pawn.getCol()] = promotedPiece;
            updateBoardDisplay();

            // Continue with the game
            gameState.toggleTurn();
            gameStatusManager.updateDisplay();
            checkGameStatus();
        });

        builder.show();
    }
    public void resetBoard() {
        // Clear the board
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                pieces[row][col] = null; // Clear pieces
                cells[row][col].setImageResource(0); // Clear cell images
            }
        }

        // Set up initial pieces
        setupInitialPieces();

        // Update the board display
        updateBoardDisplay();
    }


}