package xterminators.spellingbee.gui;

import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

class TransferableImage implements Transferable {
    private BufferedImage image;

    public TransferableImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.imageFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.imageFlavor.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (DataFlavor.imageFlavor.equals(flavor)) {
            return image;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}