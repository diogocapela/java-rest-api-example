package rest;

import java.io.*;

public class HTTPMessage {

    // Static Elements
    //==================================================================================

    private static final int CR = 13;
    private static final int LF = 10;

    private static final String VERSION = "HTTP/1.1";

    private static final String CONTENT_TYPE = "Content-type:";
    private static final String CONTENT_LENGTH = "Content-length:";
    private static final String CONNECTION = "Connection:";

    private static final String[][] knownFileExtensions = {
            {".pdf", "application/pdf"},
            {".js", "application/javascript"},
            {".txt", "text/plain"},
            {".html", "text/html"},
            {".gif", "image/gif"},
            {".png", "image/png"},
    };

    private static String readHeaderLine(DataInputStream dataInputStream) throws IOException {
        String ret = "";
        int val;
        do {
            val = dataInputStream.read();
            System.out.println(val);
            if (val == -1) break;
            if (val != CR) ret = ret + (char) val;
        } while (val != CR);
        val = dataInputStream.read(); // read LF
        //if (val == -1) throw new IOException();
        return ret;
    }

    private static void writeHeaderLine(DataOutputStream dataOutputStream, String line) throws IOException {
        dataOutputStream.write(line.getBytes(), 0, line.length());
        dataOutputStream.write(CR);
        dataOutputStream.write(LF);
    }

    // Non-static Elements
    //==================================================================================

    private boolean isRequest;
    private String method;
    private String uri;
    private String status;

    private String contentType;
    private byte[] content;

    // Constructors
    //==================================================================================

    /**
     * Creates a new rest.HTTPMessage request by receiving it from a DataInputStream.
     */
    public HTTPMessage(DataInputStream dataInputStream) throws IOException {
        String firstLine = readHeaderLine(dataInputStream);
        isRequest = !firstLine.startsWith("HTTP/");
        method = null;
        uri = "";
        content = null;
        status = null;
        contentType = null;

        String[] firstLineComp = firstLine.split(" ");

        if (isRequest && firstLineComp.length > 1) {
            method = firstLineComp[0];
            uri = firstLineComp[1];
        } else if (firstLineComp.length > 2) {
            status = firstLineComp[1] + " " + firstLineComp[2];
        }

        String headerLine;

        do {
            headerLine = readHeaderLine(dataInputStream);

            if (headerLine.toUpperCase().startsWith(CONTENT_TYPE.toUpperCase())) {
                contentType = headerLine.substring(CONTENT_TYPE.length()).trim();
            } else if (headerLine.toUpperCase().startsWith(CONTENT_LENGTH.toUpperCase())) {
                String contentLength = headerLine.substring(CONTENT_LENGTH.length()).trim();
                int len = Integer.parseInt(contentLength);
                content = new byte[len];
            }
        } while (!headerLine.isEmpty());

        // Read Content
        if (content != null) {
            dataInputStream.readFully(content, 0, content.length);
        }
    }

    /**
     * Creates a new rest.HTTPMessage response.
     */
    public HTTPMessage() {
        isRequest = true;
        method = null;
        uri = null;
        status = null;
        content = null;
        contentType = null;
    }

    // Getters
    //==================================================================================

    public String getMethod() {
        return method;
    }

    public String getURI() {
        return uri;
    }

    public String getStatus() {
        return status;
    }

    public byte[] getContent() {
        return (content);
    }

    public String getContentAsString() {
        return (new String(content));
    }

    public String getContentType() {
        return contentType;
    }

    // Setters
    //==================================================================================

    public void setRequestMethod(String method) {
        isRequest = true;
        this.method = method;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public void setResponseStatus(String status) {
        isRequest = false;
        this.status = status;
    }

    public void setContentFromString(String content, String contentType) {
        this.content = content.getBytes();
        this.contentType = contentType;
    }

    public boolean setContentFromFile(String filePath) {
        File file = new File(filePath);
        contentType = null;

        if (!file.exists()) {
            content = null;
            return false;
        }

        for (String[] fileExtension : knownFileExtensions) {
            if (filePath.endsWith(fileExtension[0])) {
                contentType = fileExtension[1];
            }
        }

        if (contentType == null) {
            contentType = "text/html";
        }

        int contentLength = (int) file.length();

        if (contentLength == 0) {
            content = null;
            contentType = null;
            return false;
        }

        content = new byte[contentLength];

        DataInputStream dataInputStream;

        try {
            dataInputStream = new DataInputStream(new FileInputStream(file));
            try {
                dataInputStream.readFully(content, 0, contentLength);
                dataInputStream.close();
            } catch (IOException ex) {
                System.out.println("Error Reading File");
                content = null;
                contentType = null;
                return false;
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File Not Found");
            content = null;
            contentType = null;
            return false;
        }
        return true;
    }

    // Custom Methods
    //==================================================================================

    public boolean send(DataOutputStream dataOutputStream) throws IOException {
        if (isRequest) {
            if (method == null || uri == null) return false;
            writeHeaderLine(dataOutputStream, method + " " + uri + " " + VERSION);
        } else {
            if (status == null) return false;
            writeHeaderLine(dataOutputStream, VERSION + " " + status);
        }

        if (content != null) {
            if (contentType != null) {
                writeHeaderLine(dataOutputStream, CONTENT_TYPE + " " + contentType);
            }
            writeHeaderLine(dataOutputStream, CONTENT_LENGTH + " " + content.length);
        }
        writeHeaderLine(dataOutputStream, CONNECTION + " close");
        writeHeaderLine(dataOutputStream, "");
        if (content != null) {
            dataOutputStream.write(content, 0, content.length);
        }
        return true;
    }

    public boolean hasContent() {
        return (content != null);
    }

}
