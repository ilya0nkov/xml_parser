package org.example;

import java.io.*;
import java.util.*;
import java.util.regex.*;

class Book {
    String id;
    String title;
    String author;
    int year;
    String genre;
    Price price;
    String isbn;
    String format;
    Publisher publisher;
    String language;
    String translator;
    List<String> awards = new ArrayList<>();
    List<Review> reviews = new ArrayList<>();

    @Override
    public String toString() {
        return "Book\nid: " + id + "\ntitle: " + title + "\nauthor: " + author + "\nyear: " + year + "\ngenre: " + genre
                + "\nprice: " + price + "\nisbn: " + isbn + "\nformat: " + format + "\npublisher: " + publisher
                + "\nlanguage: " + language + "\ntranslator: " + translator + "\nawards: " + awards + "\nreviews: " + reviews + "\n";
    }

}

class Price {
    String currency;
    double amount;

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}

class Publisher {
    String name;
    Address address;

    @Override
    public String toString() {
        return "Publisher name=\"" + name + "\", address=" + address;
    }
}

class Address {
    String city;
    String country;

    @Override
    public String toString() {
        return "city=" + city + ", country=" + country;
    }
}

class Review {
    String user;
    int rating;
    String comment;

    @Override
    public String toString() {
        return "Review user=" + user + ", rating=" + rating + ", comment=" + comment;
    }
}
public class Main {

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter path to file");
            String filename = scanner.nextLine();

            StringBuilder xmlContent = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                xmlContent.append(line.trim());
            }
            String xml = xmlContent.toString();

            List<Book> books = new ArrayList<>();

            Pattern bookPattern = Pattern.compile("<book id=\"(\\d+)\">(.*?)</book>");
            Matcher bookMatcher = bookPattern.matcher(xml);

            while (bookMatcher.find()) {
                Book currentBook = new Book();
                currentBook.id = bookMatcher.group(1);
                String bookContent = bookMatcher.group(2);

                currentBook.title = extractValue(bookContent, "title");
                currentBook.author = extractValue(bookContent, "author");
                currentBook.year = Integer.parseInt(extractValue(bookContent, "year"));
                currentBook.genre = extractValue(bookContent, "genre");

                Pattern pricePattern = Pattern.compile("<price currency=\"([^\"]+)\">(\\d+\\.\\d+)</price>");
                Matcher priceMatcher = pricePattern.matcher(bookContent);
                if (priceMatcher.find()) {
                    currentBook.price = new Price();
                    currentBook.price.currency = priceMatcher.group(1);
                    currentBook.price.amount = Double.parseDouble(priceMatcher.group(2));
                }

                currentBook.isbn = extractValue(bookContent, "isbn");
                currentBook.format = extractValue(bookContent, "format");
                currentBook.language = extractValue(bookContent, "language");
                currentBook.translator = extractValue(bookContent, "translator");

                Pattern awardPattern = Pattern.compile("<award>(.*?)</award>");
                Matcher awardMatcher = awardPattern.matcher(bookContent);
                while (awardMatcher.find()) {
                    currentBook.awards.add(awardMatcher.group(1));
                }

                String publisherContent = extractValue(bookContent, "publisher");
                if (publisherContent != null) {
                    Publisher publisher = new Publisher();
                    publisher.name = extractValue(publisherContent, "name");

                    String addressContent = extractValue(publisherContent, "address");
                    if (addressContent != null) {
                        Address address = new Address();
                        address.city = extractValue(addressContent, "city");
                        address.country = extractValue(addressContent, "country");
                        publisher.address = address;
                    }
                    currentBook.publisher = publisher;
                }

                Pattern reviewPattern = Pattern.compile("<review>(.*?)</review>");
                Matcher reviewMatcher = reviewPattern.matcher(bookContent);
                while (reviewMatcher.find()) {
                    String reviewContent = reviewMatcher.group(1);
                    Review review = new Review();
                    review.user = extractValue(reviewContent, "user");
                    review.rating = Integer.parseInt(extractValue(reviewContent, "rating"));
                    review.comment = extractValue(reviewContent, "comment");
                    currentBook.reviews.add(review);
                }

                books.add(currentBook);
            }

            for (Book book : books) {
                System.out.println(book);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String extractValue(String content, String tagName) {
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)</" + tagName + ">");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}