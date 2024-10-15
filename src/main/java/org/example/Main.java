package org.example;

import java.io.*;
import java.util.*;
import java.util.regex.*;

class Book {
    private String id;
    private String title;
    private String author;
    private int year;
    private String genre;
    private Price price;
    private String isbn;
    private String format;
    private Publisher publisher;
    private String language;
    private String translator;
    private List<String> awards = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public List<String> getAwards() {
        return awards;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    @Override
    public String toString() {
        return "Book\nid: " + id + "\ntitle: " + title + "\nauthor: " + author + "\nyear: " + year + "\ngenre: " + genre
                + "\nprice: " + price + "\nisbn: " + isbn + "\nformat: " + format + "\npublisher: " + publisher
                + "\nlanguage: " + language + "\ntranslator: " + translator + "\nawards: " + awards + "\nreviews: " + reviews + "\n";
    }
}


class Price {
    private String currency;
    private double amount;

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}


class Publisher {
    private String name;
    private Address address;

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Publisher name=\"" + name + "\", address=" + address;
    }
}


class Address {
    private String city;
    private String country;

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "city=" + city + ", country=" + country;
    }
}


class Review {
    private String user;
    private int rating;
    private String comment;

    public void setUser(String user) {
        this.user = user;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

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
                currentBook.setId(bookMatcher.group(1));
                String bookContent = bookMatcher.group(2);

                currentBook.setTitle(extractValue(bookContent, "title"));
                currentBook.setAuthor(extractValue(bookContent, "author"));
                currentBook.setYear(Integer.parseInt(extractValue(bookContent, "year")));
                currentBook.setGenre(extractValue(bookContent, "genre"));

                Pattern pricePattern = Pattern.compile("<price currency=\"([^\"]+)\">(\\d+\\.\\d+)</price>");
                Matcher priceMatcher = pricePattern.matcher(bookContent);
                if (priceMatcher.find()) {
                    Price price = new Price();
                    price.setCurrency(priceMatcher.group(1));
                    price.setAmount(Double.parseDouble(priceMatcher.group(2)));
                    currentBook.setPrice(price);
                }

                currentBook.setIsbn(extractValue(bookContent, "isbn"));
                currentBook.setFormat(extractValue(bookContent, "format"));
                currentBook.setLanguage(extractValue(bookContent, "language"));
                currentBook.setTranslator(extractValue(bookContent, "translator"));

                Pattern awardPattern = Pattern.compile("<award>(.*?)</award>");
                Matcher awardMatcher = awardPattern.matcher(bookContent);
                while (awardMatcher.find()) {
                    currentBook.getAwards().add(awardMatcher.group(1));  // Добавление награды
                }

                String publisherContent = extractValue(bookContent, "publisher");
                if (publisherContent != null) {
                    Publisher publisher = new Publisher();
                    publisher.setName(extractValue(publisherContent, "name"));  // Установка имени издателя

                    String addressContent = extractValue(publisherContent, "address");
                    if (addressContent != null) {
                        Address address = new Address();
                        address.setCity(extractValue(addressContent, "city"));  // Установка города
                        address.setCountry(extractValue(addressContent, "country"));  // Установка страны
                        publisher.setAddress(address);  // Установка адреса издателя
                    }
                    currentBook.setPublisher(publisher);  // Установка издателя в книгу
                }

                Pattern reviewPattern = Pattern.compile("<review>(.*?)</review>");
                Matcher reviewMatcher = reviewPattern.matcher(bookContent);
                while (reviewMatcher.find()) {
                    String reviewContent = reviewMatcher.group(1);
                    Review review = new Review();
                    review.setUser(extractValue(reviewContent, "user"));
                    review.setRating(Integer.parseInt(extractValue(reviewContent, "rating")));
                    review.setComment(extractValue(reviewContent, "comment"));
                    currentBook.getReviews().add(review);
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