---
layout: post
title:  "Inital documentation"
date:   2019-06-13 23:45:00 -0400
---

## This standard-libs documentation is current up to version v0.1.2
```
└── kotlin
     ├── math
     |    ├── int
     |    |    ├── abs(int i) int
     |    |    ├── pow(int a,int b) int
     |    |    ├── add(int a,int a) int
     |    |    ├── sub(int a,int a) int
     |    |    ├── mult(int a,int a) int
     |    |    ├── div(int a,int a) int
     |    |    ├── mod(int a,int a) int
     |    |    ├── asDouble(int i) double
     |    |    ├── asLong(int i) long
     |    |    ├── asBig(int i) big
     |    |    ├── ZERO() int
     |    |    ├── ONE() int
     |    |    └── TEN() int
     |    └── big
     |         ├── abs(big i) big
     |         ├── pow(big a,big b) big
     |         ├── add(big a,big b) big
     |         ├── sub(big a,big b) big
     |         ├── mult(big a,big b) big
     |         ├── div(big a,big b) big
     |         ├── asLong(big i) long
     |         ├── asDouble(big i) double
     |         ├── asInt(big i) int
     |         ├── ZERO() big
     |         ├── ONE() big
     |         └── TEN() big
     ├── io
     |    ├── out
     |    |    ├── print(string str) void
     |    |    └── println(string str) void
     |    └── in
     |         └── line() string
     └── string
          ├── concat(string a,string b) string
          ├── from
          |    ├── int(int i) string
          |    ├── big(big b) string
          ∣    ├── double(double d) string
          ∣    ├── long(long l) string
          ∣    └── boolean(boolean l) string
          └── to
               ├── int(string str) int
               ├── double(string str) double
               ├── long(string str) long
               ├── big(string str) big
               └── boolean(string str) boolean
```
